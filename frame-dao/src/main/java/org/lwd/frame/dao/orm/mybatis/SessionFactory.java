package org.lwd.frame.dao.orm.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.lwd.frame.dao.ConnectionFactory;

/**
 * @author lwd
 */
public interface SessionFactory extends ConnectionFactory<SqlSessionFactory> {
}
