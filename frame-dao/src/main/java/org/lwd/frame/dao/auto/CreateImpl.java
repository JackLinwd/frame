package org.lwd.frame.dao.auto;

import org.lwd.frame.dao.jdbc.DataSource;
import org.lwd.frame.dao.model.Model;
import org.lwd.frame.dao.model.ModelTable;
import org.lwd.frame.dao.model.ModelTables;
import org.lwd.frame.util.Io;
import org.lwd.frame.util.Logger;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * @author lwd
 */
@Repository(AutoModel.NAME + ".create")
public class CreateImpl implements Create {
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Inject
    private ModelTables modelTables;
    @Inject
    private DataSource dataSource;
    @Inject
    private Executer executer;

    @Override
    public void execute(Map<String, Set<String>> tables) {
        modelTables.getModelClasses().forEach(modelClass -> create(tables, modelTables.get(modelClass), modelClass));
    }

    private void create(Map<String, Set<String>> tables, ModelTable modelTable, Class<? extends Model> modelClass) {
        String dataSource = this.dataSource.getKey(modelTable.getDataSource());
        if (tables.containsKey(dataSource) && tables.get(dataSource).contains(modelTable.getTableName()))
            return;

        String[] array = read(modelClass);
        if (array == null)
            return;

        for (String string : array) {
            executer.execute(dataSource, string, false);
            if (string.contains("CREATE TABLE t_dao_auto"))
                tables.get(dataSource).add("t_dao_auto");
        }
    }

    private String[] read(Class<? extends Model> modelClass) {
        try {
            InputStream inputStream = modelClass.getResourceAsStream("create.sql");
            if (inputStream == null)
                return null;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            io.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();

            return outputStream.toString().split(";\n");
        } catch (IOException e) {
            logger.warn(e, "读取DDL文件[{}:create.sql]时发生异常！", modelClass);

            return null;
        }
    }
}
