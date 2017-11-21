package org.lwd.frame.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Random;
import java.util.UUID;

/**
 * Created by linwd on 2016/10/8.
 */
@Component("frame.util.generator")
public class GeneratorImpl implements Generator {
    @Inject
    protected DateTime dateTime;

    @Override
    public String random(int length) {
        StringBuilder sb  = new StringBuilder();
        char a = 'a' - 10;
        while (sb.length() < length) {
            int n = Math.abs(new Random().nextInt()) % 36;
            if (n < 10)
                n += '0';
            else
                n += a;
            sb.append((char) n);
        }

        return sb.toString();
    }

    @Override
    public String number(int length) {
        StringBuffer sb = new StringBuffer();
        while (sb.length() < length) {
            int n = Math.abs(new Random().nextInt()) % 10 + '0';
            sb.append((char) n);
        }
        return sb.toString();
    }

    @Override
    public String chars(int length) {
        StringBuffer sb = new StringBuffer();
        while (sb.length() < length) {
            int n = Math.abs(new Random().nextInt()) % 26 + 'a';
            sb.append((char) n);
        }
        return sb.toString();
    }

    @Override
    public int random(int min, int max) {
        if (min == max)
            return min;
        if (min > max)
            return Math.abs(new Random().nextInt()) % (min - max + 1) + max;
        return Math.abs(new Random().nextInt()) % (max - min + 1) + min;
    }

    @Override
    public long random(long min, long max) {
        if (min == max)
            return min;
        if (min > max)
            return Math.abs(new Random().nextInt()) % (min - max + 1) + max;
        return Math.abs(new Random().nextInt()) % (max - min + 1) + min;
    }

    @Override
    public String uuid() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String orderNo() {
        return dateTime.toString(dateTime.now(), "yyyyMMddHHmmss") + this.number(6);
    }
}