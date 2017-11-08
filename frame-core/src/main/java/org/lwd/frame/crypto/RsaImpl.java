package org.lwd.frame.crypto;

import org.lwd.frame.util.Context;
import org.lwd.frame.util.Generator;
import org.lwd.frame.util.Io;
import org.lwd.frame.util.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.inject.Inject;
import java.io.*;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author lwd
 */
@Component("frame.crypto.rsa")
public class RsaImpl implements Rsa {
    @Inject
    private Context context;
    @Inject
    private Generator generator;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Value("${frame.crypto.rsa.path:/WEB-INF/rsa}")
    private String path;
    private String absolutePath;

    @Override
    public synchronized void generate(OutputStream publicKeyDer, OutputStream publicKeyX509, OutputStream privateKey) {
        try {
            if (absolutePath == null)
                absolutePath = context.getAbsolutePath(path) + "/";
            String filename = generator.random(32);
            Runtime.getRuntime().exec("sh " + absolutePath + "generate.sh " + absolutePath + " " + filename).waitFor();

            readDer(absolutePath + filename + "/public.der", publicKeyDer);
            readPem(absolutePath + filename + "/public.pem", publicKeyX509);
            readPem(absolutePath + filename + "/private.pem", privateKey);

            Runtime.getRuntime().exec("rm -rf " + absolutePath + filename);
        } catch (Exception e) {
            logger.warn(e, "生成RSA密钥对时发生异常！");
        }
    }

    private void readDer(String input, OutputStream outputStream) throws IOException {
        InputStream inputStream = new FileInputStream(input);
        io.copy(inputStream, outputStream);
        inputStream.close();
        outputStream.close();
    }

    private void readPem(String input, OutputStream outputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(input));
        StringBuilder sb = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; )
            if (line.indexOf('-') == -1)
                sb.append(line.trim());
        reader.close();
        outputStream.write(Base64.getDecoder().decode(sb.toString()));
        outputStream.close();
    }

    @Override
    public byte[] encrypt(KeyType type, byte[] key, byte[] message) {
        if (type == null || key == null || message == null)
            return null;

        return doFinal(Cipher.ENCRYPT_MODE, type, key, message);
    }

    @Override
    public byte[] decrypt(KeyType type, byte[] key, byte[] message) {
        if (type == null || key == null || message == null)
            return null;

        return doFinal(Cipher.DECRYPT_MODE, type, key, message);
    }

    private Key getKey(KeyType type, byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        if (type == KeyType.Private)
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(key));

        return keyFactory.generatePublic(new X509EncodedKeySpec(key));
    }

    private byte[] doFinal(int mode, KeyType type, byte[] key, byte[] message) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(mode, getKey(type, key));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len = mode == Cipher.ENCRYPT_MODE ? 245 : 256;
            byte[] bytes = new byte[Math.min(len, message.length)];
            for (int i = 0, last = message.length - 1; i < message.length; i++) {
                bytes[i % len] = message[i];
                if (i % len == len - 1 || i == last) {
                    outputStream.write(cipher.doFinal(bytes));
                    bytes = new byte[Math.min(len, last - i)];
                }
            }
            outputStream.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.warn(e, "RSA" + (mode == Cipher.ENCRYPT_MODE ? "加" : "解") + "密时发生异常！");

            return null;
        }
    }
}
