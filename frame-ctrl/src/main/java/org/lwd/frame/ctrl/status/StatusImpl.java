package org.lwd.frame.ctrl.status;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lwd
 */
@Service("frame.ctrl.status")
public class StatusImpl implements Status, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Value("${frame.ctrl.status.uri:/frame/ctrl/status}")
    private String uri;
    private boolean enable;
    private JSONObject version;

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public boolean isStatus(String uri) {
        return enable && this.uri.equals(uri);
    }

    @Override
    public JSONObject execute(int counter) {
        JSONObject json = new JSONObject();
        json.put("concurrent", counter);
        json.put("timestamp", System.currentTimeMillis());
        json.put("version", version);

        return json;
    }

    @Override
    public int getContextRefreshedSort() {
        return 8;
    }

    @Override
    public void onContextRefreshed() {
        enable = !validator.isEmpty(uri);
        if (logger.isInfoEnable())
            logger.info("设置服务状态启动状态：{}。", enable);

        version();
        if (logger.isDebugEnable())
            logger.debug("设置版本信息：{}。", version);
    }

    private void version() {
        Set<String> set = new HashSet<>();
        for (String beanName : BeanFactory.getBeanNames()) {
            Object bean = BeanFactory.getBean(beanName);
            if (bean != null) {
                CodeSource source = bean.getClass().getProtectionDomain().getCodeSource();
                if (source != null)
                    set.add(source.getLocation().getPath());
            }
        }

        Map<String, Set<String>> map = new HashMap<>();
        set.forEach(path -> {
            int[] range = new int[]{path.lastIndexOf('/'), path.lastIndexOf('.')};
            if (range[0] == -1 || range[1] == -1 || range[0] > range[1])
                return;

            path = path.substring(range[0] + 1, range[1]);
            if (path.startsWith("spring"))
                return;

            int indexOf = indexOfNumber(path);
            if (indexOf == -1)
                return;

            putToMap(map, path.substring(0, indexOf - 1), path.substring(indexOf));
        });
        setVersion(map);
    }

    private int indexOfNumber(String string) {
        for (int i = 0, length = string.length(); i < length; i++) {
            char ch = string.charAt(i);
            if (ch >= '0' && ch <= '9')
                return i;
        }

        return -1;
    }

    private void putToMap(Map<String, Set<String>> map, String name, String version) {
        StringBuilder sb = new StringBuilder();
        for (String string : name.split("-")) {
            sb.append('-').append(string);
            Set<String> set = map.get(sb.toString());
            if (set == null)
                set = new HashSet<>();
            set.add(version);
            map.put(sb.toString(), set);
        }
    }

    private void setVersion(Map<String, Set<String>> map) {
        Set<String> set = new HashSet<>();
        map.forEach((key, value) -> {
            if (value.size() > 1)
                set.add(key);
        });
        set.forEach(map::remove);

        Map<String, String> versions = new HashMap<>();
        map.forEach((key, value) -> versions.put(key, value.iterator().next()));

        set.clear();
        versions.forEach((key, value) -> versions.forEach((k, v) -> {
            if (key.contains(k) && !key.equals(k) && value.equals(v))
                set.add(key);
        }));
        set.forEach(versions::remove);

        version = new JSONObject();
        versions.forEach((key, value) -> version.put(key.substring(1), value));
    }
}
