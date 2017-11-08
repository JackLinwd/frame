package org.lwd.frame.hadoop;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.lwd.frame.atomic.Closable;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.dao.model.Model;
import org.lwd.frame.dao.model.ModelTable;
import org.lwd.frame.dao.model.ModelTables;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Generator;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lwd
 */
@Component("frame.hadoop.hbase")
public class HbaseImpl implements Hbase, Closable, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    @Inject
    private ModelTables modelTables;
    @Value("${frame.hadoop.zookeeper.quorum:}")
    private String zkQuorum;
    @Value("${frame.hadoop.zookeeper.port:}")
    private String zkPort;
    private boolean disabled;
    private Configuration configuration;
    private ThreadLocal<Connection> connection = new ThreadLocal<>();

    @Override
    public <T extends Model> T findById(Class<T> modelClass, String id) {
        if (isDisabled() || modelClass == null || validator.isEmpty(id))
            return null;

        ModelTable modelTable = modelTables.get(modelClass);
        try {
            Table table = getTable(modelTable.getTableName());
            Result result = table.get(new Get(Bytes.toBytes(id)));
            if (result == null || result.isEmpty()) {
                table.close();

                return null;
            }

            T model = BeanFactory.getBean(modelClass);
            model.setId(id);
            setToModel(modelTable, model, result);
            table.close();

            return model;
        } catch (IOException e) {
            logger.warn(e, "检索HBase数据[{}:{}]时发生异常！", modelTable.getTableName(), id);

            return null;
        }
    }

    @Override
    public JSONObject findById(String tableName, String id) {
        if (isDisabled() || validator.isEmpty(tableName) || validator.isEmpty(id))
            return null;

        try {
            Table table = getTable(tableName);
            Result result = table.get(new Get(Bytes.toBytes(id)));
            if (result == null || result.isEmpty()) {
                table.close();

                return null;
            }

            JSONObject object = new JSONObject();
            setToJson(object, id, result);
            table.close();

            return object;
        } catch (IOException e) {
            logger.warn(e, "检索HBase数据[{}:{}]时发生异常！", tableName, id);

            return null;
        }
    }

    @Override
    public <T extends Model> T findOne(HbaseQuery query) {
        List<T> list = query(query.size(1));

        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public JSONObject findOneAsJson(HbaseQuery query) {
        JSONArray array = queryAsJson(query.size(1));

        return array.isEmpty() ? null : array.getJSONObject(0);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T extends Model> List<T> query(HbaseQuery query) {
        List<T> list = new ArrayList<>();
        if (isDisabled() || query == null)
            return list;

        if (query.getModelClass() == null) {
            logger.warn(null, "Model类为null，检索失败！");

            return list;
        }

        ModelTable modelTable = modelTables.get(query.getModelClass());
        try {
            Table table = getTable(modelTable.getTableName());
            ResultScanner scanner = query(table, query.getFilter());
            scanner.forEach(result -> {
                T model = BeanFactory.getBean((Class<T>) query.getModelClass());
                model.setId(Bytes.toString(result.getRow()));
                setToModel(modelTable, model, result);
                list.add(model);
            });
            scanner.close();
            table.close();
        } catch (IOException e) {
            logger.warn(e, "检索HBase数据[{}]时发生异常！", modelTable.getTableName());
        }

        return list;
    }

    private <T extends Model> void setToModel(ModelTable modelTable, T model, Result result) {
        for (Cell cell : result.rawCells())
            modelTable.set(model, Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength()),
                    Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
    }

    @Override
    public JSONArray queryAsJson(HbaseQuery query) {
        JSONArray array = new JSONArray();
        if (isDisabled() || query == null)
            return array;

        if (validator.isEmpty(query.tableName)) {
            logger.warn(null, "表名称为空，检索失败！");

            return array;
        }

        try {
            Table table = getTable(query.getTableName());
            ResultScanner scanner = query(table, query.getFilter());
            scanner.forEach(result -> {
                JSONObject object = new JSONObject();
                setToJson(object, Bytes.toString(result.getRow()), result);
                array.add(object);
            });
            scanner.close();
            table.close();
        } catch (IOException e) {
            logger.warn(e, "检索HBase数据[{}]时发生异常！", query.getTableName());
        }

        return array;
    }

    private void setToJson(JSONObject object, String id, Result result) {
        object.put("id", id);
        for (Cell cell : result.rawCells())
            object.put(Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength()),
                    Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
    }

    private ResultScanner query(Table table, Filter filter) throws IOException {
        Scan scan = new Scan();
        if (filter != null)
            scan.setFilter(filter);

        return table.getScanner(scan);
    }

    @Override
    public <T extends Model> void save(T model) {
        if (isDisabled() || model == null)
            return;

        ModelTable modelTable = modelTables.get(model.getClass());
        try {
            Table table = getTable(modelTable.getTableName());
            if (validator.isEmpty(model.getId())) {
                if (modelTable.isUuid())
                    model.setId(generator.uuid());
            } else
                delete(table, model.getId());
            if (validator.isEmpty(model.getId())) {
                table.close();

                return;
            }

            Put put = new Put(Bytes.toBytes(model.getId()));
            modelTable.getColumnNames().forEach(column -> put.addColumn(Bytes.toBytes(column), null,
                    Bytes.toBytes(converter.toString(modelTable.get(model, column)))));
            table.put(put);
            table.close();
        } catch (IOException e) {
            logger.warn(e, "保存Model数据[{}]到HBase时发生异常！", modelTable.toString(model));
        }
    }

    @Override
    public void save(String tableName, String id, Map<String, Object> map) {
        if (isDisabled() || validator.isEmpty(id) || validator.isEmpty(map))
            return;

        try {
            Table table = getTable(tableName);
            delete(table, id);
            Put put = new Put(Bytes.toBytes(id));
            map.forEach((key, value) -> put.addColumn(Bytes.toBytes(key), null, Bytes.toBytes(converter.toString(value))));
            table.put(put);
            table.close();
        } catch (IOException e) {
            logger.warn(e, "保存数据[{}:{}]到HBase[{}]时发生异常！", id, converter.toString(map), tableName);
        }
    }

    @Override
    public <T extends Model> void delete(T model) {
        if (isDisabled() || model == null || validator.isEmpty(model.getId()))
            return;

        ModelTable modelTable = modelTables.get(model.getClass());
        try {
            Table table = getTable(modelTable.getTableName());
            delete(table, model.getId());
            table.close();
        } catch (IOException e) {
            logger.warn(e, "删除HBase数据[{}:{}]时发生异常！", modelTable.getTableName(), model.getId());
        }
    }

    @Override
    public void delete(String tableName, String id) {
        if (isDisabled() || validator.isEmpty(id))
            return;

        try {
            Table table = getTable(tableName);
            delete(table, id);
            table.close();
        } catch (IOException e) {
            logger.warn(e, "删除HBase数据[{}:{}]时发生异常！", tableName, id);
        }
    }

    private Table getTable(String tableName) throws IOException {
        Connection connection = this.connection.get();
        if (connection == null) {
            connection = ConnectionFactory.createConnection(configuration);
            this.connection.set(connection);
        }

        return connection.getTable(TableName.valueOf(tableName));
    }

    private void delete(Table table, String id) throws IOException {
        table.delete(new Delete(Bytes.toBytes(id)));
    }

    private boolean isDisabled() {
        if (disabled)
            logger.warn(null, "未启用或初始化HBase[{}]！", zkQuorum);

        return disabled;
    }

    @Override
    public void close() {
        Connection connection = this.connection.get();
        if (connection == null)
            return;

        try {
            connection.close();
        } catch (IOException e) {
            logger.warn(e, "关闭HBase连接时发生异常！");
        }
        this.connection.remove();
    }

    @Override
    public int getContextRefreshedSort() {
        return 15;
    }

    @Override
    public void onContextRefreshed() {
        if (disabled = validator.isEmpty(zkQuorum)) {
            if (logger.isDebugEnable())
                logger.debug("未启用HBase。");

            return;
        }

        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", zkQuorum);
        if (!validator.isEmpty(zkPort))
            configuration.set("hbase.zookeeper.property.clientPort", zkPort);
        if (logger.isInfoEnable())
            logger.info("初始化HBase环境[{}:{}]完成。", zkQuorum, zkPort);
    }
}
