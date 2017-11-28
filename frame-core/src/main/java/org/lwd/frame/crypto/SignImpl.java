package org.lwd.frame.crypto;

import org.lwd.frame.storage.StorageListener;
import org.lwd.frame.storage.Storages;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Io;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Numeric;
import org.lwd.frame.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lwd
 */
@Component("frame.crypto.sign")
public class SignImpl implements Sign, StorageListener {
    private static final String SIGN = "sign";
    private static final String SIGN_TIME = "sign_time";

    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Validator validator;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Inject
    private Digest digest;
    @Value("${frame.crypto.sign.path:/WEB-INF/security/sign}")
    private String path;
    @Value("${frame.crypto.sign.time:10000}")
    private long time;
    private Map<String, String> map = new HashMap<>();

    @Override
    public void put(Map<String, String> map, String name) {
        if (map == null)
            return;

        map.put(SIGN_TIME, numeric.toString(System.currentTimeMillis(), "0"));
        map.put(SIGN, get(map, name));
    }

    @Override
    public boolean verify(Map<String, String> map, String name) {
        return !validator.isEmpty(map) && map.containsKey(SIGN) && map.containsKey(SIGN_TIME) &&
                System.currentTimeMillis() - numeric.toLong(map.get(SIGN_TIME)) < time && get(map, name).equals(map.get(SIGN));
    }

    protected String get(Map<String, String> map, String name) {
        List<String> list = new ArrayList<>(map.keySet());
        list.remove(SIGN);
        Collections.sort(list);

        StringBuilder sb = new StringBuilder();
        list.forEach(key -> sb.append(key).append('=').append(map.get(key)).append('&'));

        if (logger.isDebugEnable())
            logger.debug("签名参数：{}。", sb);

        return digest.md5(sb.append(getKey(name)).toString());
    }

    private String getKey(String name) {
        String key = map.get(validator.isEmpty(name) ? "" : name);
        if (key == null)
            key = map.get("");

        return key;
    }

    @Override
    public String getStorageType() {
        return Storages.TYPE_DISK;
    }

    @Override
    public String[] getScanPathes() {
        return new String[]{path};
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        Map<String, String> map = new HashMap<>();
        for (String string : converter.toArray(io.readAsString(absolutePath), "\n")) {
            string = string.trim();
            int indexOf;
            if (string.length() == 0 || string.charAt(0) == '#' || (indexOf = string.indexOf('=')) == -1)
                continue;

            map.put(string.substring(0, indexOf).trim(), string.substring(indexOf + 1).trim());
        }
        this.map.clear();
        this.map.putAll(map);

        if (logger.isInfoEnable())
            logger.info("更新签名密钥[{}]。", converter.toString(map.keySet()));
    }
}
