package org.lwd.frame.dao.orm.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.lwd.frame.dao.ConnectionSupport;
import org.lwd.frame.dao.Mode;
import org.lwd.frame.dao.jdbc.DataSource;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lwd
 */
@Repository("frame.dao.orm.hibernate.session")
public class SessionImpl extends ConnectionSupport<Session> implements org.lwd.frame.dao.orm.hibernate.Session {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private DataSource dataSource;
    @Inject
    private org.lwd.frame.dao.orm.hibernate.SessionFactory sessionFactory;
    private ThreadLocal<Map<String, Session>> sessions = new ThreadLocal<>();
    private ThreadLocal<Boolean> transactional = new ThreadLocal<>();

    @Override
    public void beginTransaction() {
        transactional.set(true);
    }

    @Override
    public Session get(String dataSource, Mode mode) {
        if (dataSource == null)
            dataSource = this.dataSource.getDefaultKey();
        if ((transactional.get() != null && transactional.get()) || !this.dataSource.hasReadonly(dataSource))
            mode = Mode.Write;
        Map<String, Session> sessions = this.sessions.get();
        if (sessions == null)
            sessions = new HashMap<>();
        String key = dataSource + mode.ordinal();
        Session session = sessions.get(key);
        if (session != null)
            return session;

        SessionFactory sessionFactory = null;
        if (mode == Mode.Read)
            sessionFactory = this.sessionFactory.getReadonly(dataSource);
        if (sessionFactory == null)
            sessionFactory = this.sessionFactory.getWriteable(dataSource);
        if (sessionFactory == null) {
            logger.warn(null, "无法获得[{}:{}]Hibernate环境！", dataSource, mode);

            throw new NullPointerException("无法获得[" + dataSource + ":" + mode + "]Hibernate环境！");
        }

        session = sessionFactory.getCurrentSession();
        if (!session.getTransaction().isActive())
            session.beginTransaction();
        sessions.put(key, session);
        this.sessions.set(sessions);

        return session;
    }

    @Override
    public void fail(Throwable throwable) {
        Map<String, Session> sessions = this.sessions.get();
        if (validator.isEmpty(sessions))
            return;

        sessions.forEach((key, session) -> session.getTransaction().rollback());
        this.sessions.remove();
        transactional.remove();

        if (logger.isDebugEnable())
            logger.debug("回滚[{}]Hibernate Session！", sessions.size());
    }

    @Override
    public void close() {
        Map<String, Session> sessions = this.sessions.get();
        if (validator.isEmpty(sessions))
            return;

        sessions.forEach((key, session) -> {
            if (session.getTransaction().isActive())
                session.getTransaction().commit();
        });
        this.sessions.remove();
        transactional.remove();

        if (logger.isDebugEnable())
            logger.debug("关闭[{}]Hibernate Session！", sessions.size());
    }
}
