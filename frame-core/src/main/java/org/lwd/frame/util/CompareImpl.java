package org.lwd.frame.util;

import org.springframework.stereotype.Component;

/**
 * Created by linwd on 2017/5/31.
 */
@Component("frame.util.compare")
public class CompareImpl implements Compare {

    @Override
    public int version(String v1, String v2) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] vA1 = v1.split("\\.");//注意此处为正则匹配，不能用"."；
        String[] vA2 = v2.split("\\.");
        int idx = 0;
        int minLength = Math.min(vA1.length, vA2.length);//取最小长度值
        int diff = 0;
        while (idx < minLength
                && (diff = vA1[idx].length() - vA2[idx].length()) == 0//先比较长度
                && (diff = vA1[idx].compareTo(vA2[idx])) == 0) {//再比较字符
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : vA1.length - vA2.length;
        return diff;
    }
}
