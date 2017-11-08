package org.lwd.frame.dao.model;

import org.hibernate.annotations.GenericGenerator;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Validator;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lwd
 */
@Repository("frame.model.tables")
public class ModelTablesImpl implements ModelTables, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Optional<Set<Model>> models;
    private Map<Class<? extends Model>, ModelTable> map;

    @Override
    public ModelTable get(Class<? extends Model> modelClass) {
        ModelTable modelTable = map.get(modelClass);
        if (modelTable == null)
            throw new NullPointerException("无法获得Model类[" + modelClass + "]对应的ModelTable实例！");

        return modelTable;
    }

    @Override
    public Set<Class<? extends Model>> getModelClasses() {
        return new HashSet<>(map.keySet());
    }

    @Override
    public int getContextRefreshedSort() {
        return 2;
    }

    @Override
    public void onContextRefreshed() {
        if (map != null)
            return;

        map = new ConcurrentHashMap<>();
        models.ifPresent(set -> set.forEach(model -> parse(model.getClass())));
    }

    private void parse(Class<? extends Model> modelClass) {
        Table table = modelClass.getAnnotation(Table.class);
        if (table == null)
            return;

        ModelTable modelTable = BeanFactory.getBean(ModelTable.class);
        modelTable.setModelClass(modelClass);
        DataSource dataSource = modelClass.getAnnotation(DataSource.class);
        if (dataSource != null)
            modelTable.setDataSource(dataSource.key());
        modelTable.setTableName(table.name());

        Memory memory = modelClass.getAnnotation(Memory.class);
        if (memory != null)
            modelTable.setMemoryName(memory.name());

        Method[] methods = modelClass.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.length() < 3)
                continue;

            if (name.equals("getId")) {
                modelTable.setIdColumnName(method.getAnnotation(Column.class).name());
                GenericGenerator generator = method.getAnnotation(GenericGenerator.class);
                if (generator != null && generator.name().toLowerCase().startsWith("uuid"))
                    modelTable.setUuid(true);

                continue;
            }

            String propertyName = converter.toFirstLowerCase(name.substring(3));
            if (startsWith(name, 'g')) {
                modelTable.addGetMethod(propertyName, method);
                Column column = method.getAnnotation(Column.class);
                if (column != null)
                    modelTable.addColumn(propertyName, column);

                continue;
            }

            if (startsWith(name, 's'))
                modelTable.addSetMethod(propertyName, method);
        }

        map.put(modelClass, modelTable);
    }

    private boolean startsWith(String name, char ch) {
        return name.charAt(0) == ch && name.charAt(1) == 'e' && name.charAt(2) == 't';
    }
}
