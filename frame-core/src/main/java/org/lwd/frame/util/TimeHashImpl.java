package org.lwd.frame.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by linwd on 2016/10/8.
 */
@Component("frame.util.timeHash")
public class TimeHashImpl implements TimeHash {

    @Value("${frame.util.time-hash.range:0}")
    protected int range = 0;
    private int base = 100000;


    @Override
    public int generate() {
        long time = System.currentTimeMillis();
        long n0 = time >> 10;
        long n1 = time >> (n0 % base) * (n0 / base);
        long n2 = time >> time % 90 + 10;
        long n3 = time >> n2 * base + n1 % base;
        long n4 = time >> n3;
        while (n4 >= 100) n4 = n4 >> 1;
        return (int) (n3 * 100 + n4);
    }

    @Override
    public boolean isEnable() {
        return range > 0;
    }

    @Override
    public boolean valid(int code) {
        if (range < 1) return true;

        if (code < 100000000) return false;

        int x = code / 100;
        while (x >= 100) x = x >> 1;
        if (x != code % 100) return false;

        x = (code / 100) % base;
        long time = System.currentTimeMillis();
        for (int i = 1; i <= range << 1; i++) {
            if (x == generate(time, -1 * i) || x == generate(time, i))
                return true;
        }

        return false;
    }

    private int generate(long time, int offset) {
        long t = (time + offset * 1000) >> 10;
        long n = (t % base) * (t / base);
        return (int) (n % base);
    }
}
