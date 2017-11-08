package org.lwd.frame.dao.dialect;

import org.springframework.stereotype.Repository;

/**
 * @author lwd
 */
@Repository("frame.dao.dialect.mysql")
public class MysqlDialect extends DialectSupport implements Dialect {
    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public String getDriver() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String getUrl(String ip, String schema) {
        return "jdbc:mysql://" + ip + "/" + schema + "?useUnicode=true&characterEncoding=utf8&autoReconnect=true";
    }

    @Override
    public String selectTables(String schema) {
        return "show tables";
    }

    @Override
    public String getHibernateDialect() {
        return "org.hibernate.dialect.MySQLDialect";
    }

    @Override
    public void appendPagination(StringBuilder sql, int size, int page) {
        sql.append(" LIMIT ").append(size * (page - 1)).append(',').append(size);
    }
}
