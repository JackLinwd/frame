package org.lwd.frame.dao.jdbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.dao.Mode;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author lwd
 */
abstract class JdbcSupport<T extends PreparedStatement> implements Jdbc {
    @Inject
    Validator validator;
    @Inject
    Converter converter;
    @Inject
    Logger logger;
    @Inject
    private DataSource dataSource;
    @Inject
    private Connection connection;
    @Inject
    private BatchUpdate batchUpdate;

    @Override
    public SqlTable query(String sql, Object[] args) {
        return query(null, sql, args);
    }

    @Override
    public JSONArray queryAsJson(String sql, Object[] args) {
        return queryAsJson(null, sql, args);
    }

    SqlTable query(ResultSet rs) throws SQLException {
        SqlTable sqlTable = BeanFactory.getBean(SqlTable.class);
        sqlTable.set(rs);
        rs.close();

        return sqlTable;
    }

    JSONArray queryAsJson(ResultSet rs) throws SQLException {
        JSONArray array = new JSONArray();
        String[] columns = new String[rs.getMetaData().getColumnCount()];
        for (int i = 0; i < columns.length; i++)
            columns[i] = rs.getMetaData().getColumnLabel(i + 1);
        for (; rs.next(); ) {
            JSONObject object = new JSONObject();
            for (String column : columns)
                object.put(column, converter.toString(rs.getObject(column)));
            array.add(object);
        }
        rs.close();

        return array;
    }

    @Override
    public int update(String sql, Object[] args) {
        return update(null, sql, args);
    }

    @Override
    public int update(String dataSource, String sql, Object[] args) {
        if (batchUpdate.collect(dataSource, sql, args))
            return -1;

        try {
            long time = System.currentTimeMillis();
            T pstmt = newPreparedStatement(dataSource, Mode.Write, sql);
            setArgs(pstmt, args);
            int n = pstmt.executeUpdate();
            pstmt.close();

            if (logger.isDebugEnable())
                logger.debug("执行SQL[{}:{}:{}:{}]更新操作。", dataSource, sql, converter.toString(args),
                        System.currentTimeMillis() - time);

            return n;
        } catch (SQLException e) {
            logger.warn(e, "执行SQL[{}:{}:{}]更新时发生异常！", dataSource, sql, converter.toString(args));

            throw new RuntimeException(e);
        }
    }

    T newPreparedStatement(String dataSource, Mode mode, String sql) throws SQLException {
        return newPreparedStatement(getConnection(dataSource, mode), sql);
    }

    abstract T newPreparedStatement(java.sql.Connection connection, String sql) throws SQLException;

    private java.sql.Connection getConnection(String dataSource, Mode mode) throws SQLException {
        java.sql.Connection connection = this.connection.get(dataSource, mode);
        if (connection == null)
            throw new NullPointerException("无法获得数据库[" + mode + "]连接！");

        return connection;
    }

    /**
     * 设置参数集。
     *
     * @param pstmt PreparedStatement实例。
     * @param args  参数集。
     * @throws SQLException 未处理SQLException异常。
     */
    void setArgs(T pstmt, Object[] args) throws SQLException {
        if (validator.isEmpty(args))
            return;

        for (int i = 0; i < args.length; i++)
            pstmt.setObject(i + 1, args[i]);
    }

    @Override
    public void fail(Throwable throwable) {
        connection.fail(throwable);
    }

    @Override
    public void close() {
        connection.close();
    }
}
