package org.lwd.frame.dao.auto;

import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.crypto.Digest;
import org.lwd.frame.dao.jdbc.DataSource;
import org.lwd.frame.dao.jdbc.Sql;
import org.lwd.frame.dao.jdbc.SqlTable;
import org.lwd.frame.dao.orm.lite.LiteOrm;
import org.lwd.frame.dao.orm.lite.LiteQuery;
import org.lwd.frame.util.DateTime;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lwd
 */
@Repository(AutoModel.NAME + ".executer")
public class ExecuterImpl implements Executer, ContextRefreshedListener {
    @Inject
    private Digest digest;
    @Inject
    private DateTime dateTime;
    @Inject
    private DataSource dataSource;
    @Inject
    private Sql sql;
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private Create create;
    @Inject
    private Memory memory;
    @Inject
    private Update update;
    private Map<String, Set<String>> map;

    @Override
    public int execute(String dataSource, String sql, boolean state0) {
        sql = sql.trim();
        if (sql.equals("") || sql.charAt(0) == '-')
            return 0;

        String md5 = digest.md5(dataSource + sql);
        if (hasAutoTable()) {
            if (state0 && liteOrm.findOne(new LiteQuery(AutoModel.class).where("c_md5=? and c_state=?"), new Object[]{md5, 0}) != null) {
                liteOrm.close();

                return 0;
            }

            AutoModel auto = new AutoModel();
            auto.setMd5(md5);
            auto.setDataSource(dataSource);
            auto.setSql(sql);
            auto.setTime(dateTime.now());
            liteOrm.save(auto);
        }

        int n = this.sql.update(dataSource, sql, new Object[0]);
        this.sql.close();

        return n;
    }

    private boolean hasAutoTable() {
        return map.get(dataSource.getDefaultKey()).contains(AutoModel.class.getAnnotation(Table.class).name());
    }

    @Override
    public int getContextRefreshedSort() {
        return 4;
    }

    @Override
    public void onContextRefreshed() {
        map = new HashMap<>();
        dataSource.getDialects().forEach((key, dialect) -> {
            Set<String> set = new HashSet<>();
            SqlTable sqlTable = sql.query(key, dialect.selectTables(dataSource.getConfig(key).getString("schema")), null);
            for (int i = 0; i < sqlTable.getRowCount(); i++)
                set.add(sqlTable.get(i, 0));
            sql.close();
            map.put(key, set);
        });

        create.execute(map);
        memory.execute();
        update.execute();
    }
}
