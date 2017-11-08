package org.lwd.frame.dao.orm.mybatis;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.lwd.frame.dao.Mode;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * @author lwd
 */
@Repository("frame.dao.orm.mybatis.orm")
public class MybatisOrmImpl implements MybatisOrm {
    @Inject
    private Session session;

    @Override
    public <T> T getMapper(String dataSource, Mode mode, Class<T> mapperClass) {
        return session.get(dataSource, mode == null ? Mode.Write : mode).getMapper(mapperClass);
    }

    @Override
    public <T> T selectOne(MybatisBuilder builder) {
        return getRead(builder).selectOne(builder.getStatement(), builder.getParameter());
    }

    @Override
    public <T> List<T> selectList(MybatisBuilder builder) {
        return getRead(builder).selectList(builder.getStatement(), builder.getParameter(), getRowBounds(builder));
    }

    @Override
    public <K, V> Map<K, V> selectMap(MybatisBuilder builder) {
        return getRead(builder).selectMap(builder.getStatement(), builder.getParameter(), builder.getMapKey(), getRowBounds(builder));
    }

    @Override
    public void select(MybatisBuilder builder) {
        getRead(builder).select(builder.getStatement(), builder.getParameter(), getRowBounds(builder), builder.getResultHandler());
    }

    private SqlSession getRead(MybatisBuilder builder) {
        return session.get(builder.getDataSource(), Mode.Read);
    }

    private RowBounds getRowBounds(MybatisBuilder builder) {
        return builder.getRowBounds() == null ? RowBounds.DEFAULT : builder.getRowBounds();
    }

    @Override
    public int insert(MybatisBuilder builder) {
        return getWrite(builder).insert(builder.getStatement(), builder.getParameter());
    }

    @Override
    public int update(MybatisBuilder builder) {
        return getWrite(builder).update(builder.getStatement(), builder.getParameter());
    }

    @Override
    public int delete(MybatisBuilder builder) {
        return getWrite(builder).delete(builder.getStatement(), builder.getParameter());
    }

    private SqlSession getWrite(MybatisBuilder builder) {
        return session.get(builder.getDataSource(), Mode.Write);
    }

    @Override
    public void fail(Throwable throwable) {
        session.fail(throwable);
    }

    @Override
    public void close() {
        session.close();
    }
}
