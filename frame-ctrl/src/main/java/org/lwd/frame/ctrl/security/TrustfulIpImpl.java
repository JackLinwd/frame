package org.lwd.frame.ctrl.security;

import org.lwd.frame.storage.StorageListener;
import org.lwd.frame.storage.Storages;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Io;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lwd
 */
@Controller("frame.ctrl.security.trustful-ip")
public class TrustfulIpImpl implements TrustfulIp, StorageListener {
    @Inject
    private Converter converter;
    @Inject
    private Validator validator;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Value("${frame.ctrl.security.trustful-ip:/WEB-INF/security/trustful-ip}")
    private String trustfulIp;
    private Set<String> ips = new HashSet<>();
    private Set<String> patterns = new HashSet<>();

    @Override
    public boolean contains(String ip) {
        if (ips.contains(ip))
            return true;

        for (String pattern : patterns)
            if (validator.isMatchRegex(pattern, ip))
                return true;

        return false;
    }

    @Override
    public String getStorageType() {
        return Storages.TYPE_DISK;
    }

    @Override
    public String[] getScanPathes() {
        return new String[]{trustfulIp};
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        Set<String> ips = new HashSet<>();
        Set<String> patterns = new HashSet<>();
        for (String string : converter.toArray(io.readAsString(absolutePath), "\n")) {
            if (validator.isEmpty(string))
                continue;

            string = string.trim();
            if (string.charAt(0) == '#')
                continue;

            if (string.charAt(0) == 'r' && string.charAt(1) == 'g')
                patterns.add(string.substring(2));
            else
                ips.add(string);
        }
        this.ips = ips;
        this.patterns = patterns;

        if (logger.isInfoEnable())
            logger.info("更新信任IP[{}|{}]集。", converter.toString(ips), converter.toString(patterns));
    }
}
