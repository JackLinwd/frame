package org.lwd.frame.dao.jdbc;

import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Logger;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lwd
 */
@Repository("frame.dao.jdbc.batch-update")
public class BatchUpdateImpl implements BatchUpdate {
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    @Inject
    private Sql sql;
    private ThreadLocal<Set<String>> tlIgnore = new ThreadLocal<>();
    private ThreadLocal<List<String>> tlDataSource = new ThreadLocal<>();
    private ThreadLocal<List<String>> tlSql = new ThreadLocal<>();
    private ThreadLocal<List<Object[]>> tlArgs = new ThreadLocal<>();

    @Override
    public void begin() {
        tlIgnore.set(new HashSet<>());
        tlDataSource.set(new ArrayList<>());
        tlSql.set(new ArrayList<>());
        tlArgs.set(new ArrayList<>());
    }

    @Override
    public void ignore(String sql) {
        Set<String> set = tlIgnore.get();
        if (set != null)
            set.add(sql);
    }

    @Override
    public boolean collect(String dataSource, String sql, Object[] args) {
        if (tlDataSource.get() == null)
            return false;

        for (String string : tlIgnore.get())
            if (sql.contains(string))
                return false;

        tlDataSource.get().add(dataSource);
        tlSql.get().add(sql);
        tlArgs.get().add(args);
        if (logger.isDebugEnable())
            logger.debug("收集SQL[{}:{}:{}]。", dataSource, sql, converter.toString(args));

        return true;
    }

    @Override
    public void commit() {
        if (tlDataSource.get() == null)
            return;

        List<String> dataSources = tlDataSource.get();
        List<String> sqls = tlSql.get();
        List<Object[]> args = tlArgs.get();
        clear();
        try {
            long time = System.currentTimeMillis();
            for (int i = 0, size = dataSources.size(); i < size; i++)
                sql.update(dataSources.get(i), sqls.get(i), args.get(i));
            sql.close();
            if (logger.isDebugEnable())
                logger.debug("批量执行收集的SQL[{}:{}:{}:{}]。", converter.toString(dataSources),
                        converter.toString(sqls), converter.toString(args), System.currentTimeMillis() - time);
        } catch (Throwable throwable) {
            logger.warn(throwable, "批量执行收集的SQL[{}:{}:{}]时发生异常！", converter.toString(dataSources),
                    converter.toString(sqls), converter.toString(args));
        }
    }

    @Override
    public void clear() {
        tlIgnore.remove();
        tlDataSource.remove();
        tlSql.remove();
        tlArgs.remove();
    }
}
