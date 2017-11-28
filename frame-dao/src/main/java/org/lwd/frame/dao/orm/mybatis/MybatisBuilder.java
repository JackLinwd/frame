package org.lwd.frame.dao.orm.mybatis;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * MyBatis参数构造器。
 *
 * @author lwd
 */
public class MybatisBuilder {
    private String dataSource;
    private String statement;
    private Object parameter;
    private String mapKey;
    private RowBounds rowBounds;
    private ResultHandler resultHandler;

    /**
     * 设置数据源。
     *
     * @param dataSource 数据源名称。
     * @return 当前MybatisBuilder实例。
     */
    public MybatisBuilder dataSource(String dataSource) {
        this.dataSource = dataSource;

        return this;
    }

    /**
     * 设置执行ID。
     *
     * @param statement 执行ID。
     * @return 当前MybatisBuilder实例。
     */
    public MybatisBuilder statement(String statement) {
        this.statement = statement;

        return this;
    }

    /**
     * 设置参数。
     *
     * @param parameter 参数。
     * @return 当前MybatisBuilder实例。
     */
    public MybatisBuilder parameter(Object parameter) {
        this.parameter = parameter;

        return this;
    }

    /**
     * 设置Map Key。
     *
     * @param mapKey Map Key。
     * @return 当前MybatisBuilder实例。
     */
    public MybatisBuilder mapKey(String mapKey) {
        this.mapKey = mapKey;

        return this;
    }

    /**
     * 设置检索范围。
     *
     * @param rowBounds 检索范围。
     * @return 当前MybatisBuilder实例。
     */
    public MybatisBuilder rowBounds(RowBounds rowBounds) {
        this.rowBounds = rowBounds;

        return this;
    }

    /**
     * 设置检索结果处理器。
     *
     * @param resultHandler 检索结果处理器。
     * @return 当前MybatisBuilder实例。
     */
    public MybatisBuilder resultHandler(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;

        return this;
    }

    /**
     * 获取数据源。
     *
     * @return 数据源。
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * 获取执行ID。
     *
     * @return 执行ID。
     */
    public String getStatement() {
        return statement;
    }

    /**
     * 获取参数。
     *
     * @return 参数。
     */
    public Object getParameter() {
        return parameter;
    }

    /**
     * 获取Map Key。
     *
     * @return Map Key。
     */
    public String getMapKey() {
        return mapKey;
    }

    /**
     * 获取检索范围。
     *
     * @return 检索范围。
     */
    public RowBounds getRowBounds() {
        return rowBounds;
    }

    /**
     * 获取检索结果处理器。
     *
     * @return 检索结果处理器。
     */
    public ResultHandler getResultHandler() {
        return resultHandler;
    }
}
