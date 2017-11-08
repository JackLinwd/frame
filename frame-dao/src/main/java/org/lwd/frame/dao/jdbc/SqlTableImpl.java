package org.lwd.frame.dao.jdbc;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lwd
 */
@Repository("frame.dao.jdbc.sql-table")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SqlTableImpl implements SqlTable {
    protected String[] names;
    protected Map<String, Integer> map;
    protected List<List<Object>> list;
    protected int rowCount;
    protected int columnCount;

    @Override
    public String[] getNames() {
        return names;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(int row, int column) {
        if (row >= rowCount || column >= columnCount)
            throw new NullPointerException("无法获得[" + row + "," + column + "]数据！");

        return (T) list.get(row).get(column);
    }

    @Override
    public <T> T get(int row, String columnName) {
        Integer column = map.get(columnName);
        if (row >= rowCount || column == null)
            throw new NullPointerException("无法获得[" + row + "," + columnName + "]数据！");

        return get(row, column);
    }

    @Override
    public void set(ResultSet rs) throws SQLException {
        columnCount = rs.getMetaData().getColumnCount();
        names = new String[columnCount];
        map = new HashMap<>();
        list = new ArrayList<>();
        for (int i = 0; i < columnCount; i++) {
            names[i] = rs.getMetaData().getColumnLabel(i + 1).toLowerCase();
            map.put(names[i], i);
        }

        for (; rs.next(); ) {
            list.add(new ArrayList<>());
            for (int i = 1; i <= columnCount; i++)
                list.get(list.size() - 1).add(rs.getObject(i));
        }
        rowCount = list.size();
    }
}
