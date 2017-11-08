package org.lwd.frame.ctrl.context;

import org.lwd.frame.crypto.Sign;
import org.lwd.frame.ctrl.Coder;
import org.lwd.frame.dao.model.Model;
import org.lwd.frame.dao.model.ModelTable;
import org.lwd.frame.dao.model.ModelTables;
import org.lwd.frame.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * @author lwd
 */
@Controller("frame.ctrl.context.request")
public class RequestImpl implements Request, RequestAware {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private DateTime dateTime;
    @Inject
    private Logger logger;
    @Inject
    private Sign sign;
    @Inject
    private ModelTables modelTables;
    @Inject
    private Optional<Coder> coder;
    @Value("${frame.ctrl.context.request.sign:frame-ctrl-sign}")
    private String signName;
    private ThreadLocal<RequestAdapter> adapter = new ThreadLocal<>();

    @Override
    public String get(String name) {
        return adapter.get() == null ? null : adapter.get().get(name);
    }

    @Override
    public int getAsInt(String name) {
        return numeric.toInt(get(name));
    }

    @Override
    public int getAsInt(String name, int defaultValue) {
        return numeric.toInt(get(name), defaultValue);
    }

    @Override
    public long getAsLong(String name) {
        return numeric.toLong(get(name));
    }

    @Override
    public long getAsLong(String name, long defaultValue) {
        return numeric.toLong(get(name), defaultValue);
    }

    @Override
    public boolean getAsBoolean(String name) {
        return converter.toBoolean(get(name));
    }

    @Override
    public Date getAsDate(String name) {
        return dateTime.toDate(get(name));
    }

    @Override
    public java.sql.Date getAsSqlDate(String name) {
        Date date = getAsDate(name);

        return date == null ? null : new java.sql.Date(date.getTime());
    }

    @Override
    public String[] getAsArray(String name) {
        if (adapter.get() == null)
            return null;

        String[] array = adapter.get().getAsArray(name);

        return array == null ? converter.toArray(get(name), ",") : array;
    }

    @Override
    public Map<String, String> getMap() {
        if (adapter.get() == null)
            return null;

        Map<String, String> map = adapter.get().getMap();

        return coder.isPresent() ? coder.get().decode(map) : map;
    }

    @Override
    public String getFromInputStream() {
        return adapter.get() == null ? null : adapter.get().getFromInputStream();
    }

    @Override
    public <T extends Model> T setToModel(T model) {
        if (adapter.get() == null)
            return model;

        Map<String, String> map = getMap();
        if (validator.isEmpty(map))
            return model;

        ModelTable modelTable = modelTables.get(model.getClass());
        try {
            for (String name : map.keySet()) {
                if ("id".equals(name)) {
                    model.setId(map.get(name));

                    continue;
                }

                fillToModel(modelTable, model, name, map.get(name));
            }
        } catch (Exception e) {
            logger.warn(e, "设置参数到Model时发生异常！");
        }

        return model;
    }

    private <T extends Model> void fillToModel(ModelTable modelTable, T model, String name, String value) throws NoSuchMethodException, SecurityException {
        if ((name.endsWith("[id]") || name.endsWith(".id")) && name.indexOf('[') == name.lastIndexOf('[') && name.indexOf('.') == name.lastIndexOf('.')) {
            modelTable.set(model, name.substring(0, name.indexOf('[') + name.indexOf('.') + 1), value);

            return;
        }

        if (!validator.isMatchRegex("[a-z0-9A-Z]+", name) || value == null)
            return;

        modelTable.set(model, name, value);
    }

    @Override
    public boolean checkSign() {
        return sign.verify(getMap(), get(signName));
    }

    @Override
    public void putSign(Map<String, String> map) {
        sign.put(map, get(signName));
    }

    @Override
    public String getServerName() {
        return adapter.get() == null ? null : adapter.get().getServerName();
    }

    @Override
    public int getServerPort() {
        return adapter.get() == null ? 0 : adapter.get().getServerPort();
    }

    @Override
    public String getContextPath() {
        return adapter.get() == null ? null : adapter.get().getContextPath();
    }

    @Override
    public String getUrl() {
        return adapter.get() == null ? null : adapter.get().getUrl();
    }

    @Override
    public String getUri() {
        return adapter.get() == null ? null : adapter.get().getUri();
    }

    @Override
    public String getMethod() {
        return adapter.get() == null ? null : adapter.get().getMethod();
    }

    @Override
    public void set(RequestAdapter adapter) {
        this.adapter.set(adapter);
    }
}
