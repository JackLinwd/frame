package org.lwd.frame.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
  * @author lwd
  */
@Component("frame.util.time-hash")
class TimeHashImpl extends TimeHash {
    @Value("${frame.util.time-hash.range:0}")
    protected var range: Int = 0
    private val base: Int = 100000

    override def generate(): Int = {
        val time: Long = System.currentTimeMillis
        val n0: Long = time >> 10
        val n1: Long = (n0 % base) * (n0 / base)
        val n2: Long = time % 90 + 10
        val n3: Long = n2 * base + n1 % base
        var n4: Long = n3
        while (n4 >= 100) n4 = n4 >> 1

        (n3 * 100 + n4).toInt
    }

    override def isEnable: Boolean = range > 0

    override def valid(code: Int): Boolean = {
        if (range < 1) return true

        if (code < 100000000) return false

        var x: Int = code / 100
        while (x >= 100) x = x >> 1
        if (x != code % 100) return false

        x = (code / 100) % base
        val time = System.currentTimeMillis
        for (i <- 0 to range >> 1) if (x == generate(time, -1 * i) || x == generate(time, i)) return true

        false
    }

    def generate(time: Long, offset: Int): Int = {
        val t: Long = (time + offset * 1000) >> 10
        val n: Long = (t % base) * (t / base)
        (n % base).toInt
    }
}
