package org.lwd.frame.dao.jdbc;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.lwd.frame.dao.orm.hibernate.Session;
import org.lwd.frame.util.Logger;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * @author lwd
 */
@Aspect
@Repository("frame.dao.jdbc.transactional")
public class Transactional {
    @Inject
    private Logger logger;
    @Inject
    private Connection connection;
    @Inject
    private Session session;

    @Around("execution(* *(..)) && @annotation(javax.transaction.Transactional)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        if (logger.isDebugEnable())
            logger.debug("开始[{}]事务控制。", point.getSignature().getName());

        connection.beginTransaction();
        session.beginTransaction();
        Object object = point.proceed();
        session.close();
        connection.close();

        if (logger.isDebugEnable())
            logger.debug("事务[{}]控制结束。", point.getSignature().getName());

        return object;
    }
}
