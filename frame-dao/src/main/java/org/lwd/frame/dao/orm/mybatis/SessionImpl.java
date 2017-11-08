package org.lwd.frame.dao.orm.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
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
@Repository("frame.dao.orm.mybatis.session")
public class SessionImpl extends ConnectionSupport<SqlSession> implements Session {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private DataSource dataSource;
    @Inject
    private SessionFactory sessionFactory;
    private ThreadLocal<Map<String, SqlSession>> sessions = new ThreadLocal<>();
    private ThreadLocal<Boolean> transactional = new ThreadLocal<>();

    @Override
    public void beginTransaction() {
        transactional.set(true);
    }

    @Override
    public SqlSession get(String dataSource, Mode mode) {
        if (dataSource == null)
            dataSource = this.dataSource.getDefaultKey();
        if ((transactional.get() != null && transactional.get()) || !this.dataSource.hasReadonly(dataSource))
            mode = Mode.Write;
        Map<String, SqlSession> sessions = this.sessions.get();
        if (sessions == null)
            sessions = new HashMap<>();
        String key = dataSource + mode.ordinal();
        SqlSession session = sessions.get(key);
        if (session != null)
            return session;

        SqlSessionFactory sessionFactory = null;
        if (mode == Mode.Read)
            sessionFactory = this.sessionFactory.getReadonly(dataSource);
        if (sessionFactory == null)
            sessionFactory = this.sessionFactory.getWriteable(dataSource);
        if (sessionFactory == null) {
            logger.warn(null, "无法获得[{}:{}]MyBatis环境！", dataSource, mode);

            throw new NullPointerException("无法获得[" + dataSource + ":" + mode + "]MyBatis环境！");
        }

        session = sessionFactory.openSession(mode == Mode.Read);
        sessions.put(key, session);
        this.sessions.set(sessions);

        return session;
    }

    @Override
    public void fail(Throwable throwable) {
        Map<String, SqlSession> sessions = this.sessions.get();
        if (validator.isEmpty(sessions))
            return;

        sessions.forEach((key, session) -> {
            session.rollback();
            session.close();
        });
        this.sessions.remove();
        transactional.remove();

        if (logger.isDebugEnable())
            logger.debug("回滚[{}]MyBatis Session！", sessions.size());
    }

    @Override
    public void close() {
        Map<String, SqlSession> sessions = this.sessions.get();
        if (validator.isEmpty(sessions))
            return;

        sessions.forEach((key, session) -> {
            session.commit();
            session.close();
        });
        this.sessions.remove();
        transactional.remove();

        if (logger.isDebugEnable())
            logger.debug("关闭[{}]MyBatis Session！", sessions.size());
    }
}
