package org.lwd.frame.crypto;

import org.lwd.frame.util.Validator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lwd
 */
@Component("frame.crypto.xor-shift")
public class XorShiftImpl implements XorShift {
    @Inject
    private Validator validator;

    @Override
    public byte[] encrypt(byte[] key, byte[] message) {
        if (validator.isEmpty(key) || validator.isEmpty(message))
            return null;

        byte[] k = key(key, message.length);
        byte[] msg = copy(message);
        xor(k, msg);
        for (int i = 0; i < msg.length; i++)
            shift(k, msg, i);

        return msg;
    }

    @Override
    public byte[] decrypt(byte[] key, byte[] message) {
        if (validator.isEmpty(key) || validator.isEmpty(message))
            return null;

        byte[] k = key(key, message.length);
        byte[] msg = copy(message);
        for (int i = msg.length - 1; i > -1; i--)
            shift(k, msg, i);
        xor(k, msg);

        return msg;
    }

    private byte[] copy(byte[] message) {
        byte[] msg = new byte[message.length];
        System.arraycopy(message, 0, msg, 0, message.length);

        return msg;
    }

    private byte[] key(byte[] key, int length) {
        if (key.length >= length)
            return key;

        byte[] k = new byte[length];
        int i = 0;
        for (; i < key.length; i++)
            k[i] = key[i];
        for (; i < length; i++)
            k[i] = (byte) (key[i % key.length] ^ k[i >> 1]);

        return k;
    }

    private void xor(byte[] key, byte[] message) {
        for (int i = 0; i < message.length; i++)
            message[i] ^= key[i % key.length];
    }

    private void shift(byte[] key, byte[] message, int i) {
        int n = (key[i] & 0xff) % message.length;
        byte by = message[i];
        message[i] = message[n];
        message[n] = by;
    }
}
