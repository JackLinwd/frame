package org.lwd.frame.crypto;

import org.springframework.stereotype.Component;

/**
 * @author lwd
 */
@Component("frame.crypto.des3")
public class Des3Impl extends CipherSupport implements Des3 {
    @Override
    protected String getAlgorithm() {
        return "DESede";
    }

    @Override
    protected boolean validate(byte[] key) {
        if (key.length == 24)
            return true;

        logger.warn(null, "3DES密钥[{}]长度[{}]必须是24个字节！", new String(key), key.length);

        return false;
    }
}
