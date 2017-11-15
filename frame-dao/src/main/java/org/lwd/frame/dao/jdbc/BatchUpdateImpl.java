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

        tlIgnore.remove();
        List<String> dataSource = tlDataSource.get();
        tlDataSource.remove();
        List<String> sql = tlSql.get();
        tlSql.remove();
        List<Object[]> args = tlArgs.get();
        tlArgs.remove();

        long time = System.currentTimeMillis();
        int size = dataSource.size();
        for (int i = 0; i < size; i++)
            this.sql.update(dataSource.get(i), sql.get(i), args.get(i));
        this.sql.close();
        if (logger.isDebugEnable())
            logger.debug("批量执行收集的SQL[{}:{}]。", size, System.currentTimeMillis() - time);
    }

    @Override
    public void cancel() {
        tlIgnore.remove();
        tlDataSource.remove();
        tlSql.remove();
        tlArgs.remove();
    }
}
