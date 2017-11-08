package org.lwd.frame.util

import java.util.UUID

import org.springframework.stereotype.Component

import scala.util.Random

/**
  * @author lwd
  */
@Component("frame.util.generator")
class GeneratorImpl extends Generator {
    override def random(length: Int): String = {
        val string: StringBuilder = new StringBuilder
        val a = 'a' - 10
        while (string.length < length) {
            var n: Int = Math.abs(Random.nextInt) % 36
            if (n < 10)
                n += '0'
            else
                n += a
            string += n.toChar
        }

        string.toString
    }

    override def number(length: Int): String = {
        val string: StringBuilder = new StringBuilder
        while (string.length < length) {
            val n: Int = Math.abs(Random.nextInt) % 10 + '0'
            string += n.toChar
        }

        string.toString
    }

    override def chars(length: Int): String = {
        val string: StringBuilder = new StringBuilder
        while (string.length < length) {
            val n: Int = Math.abs(Random.nextInt) % 26 + 'a'
            string += n.toChar
        }

        string.toString
    }

    override def random(min: Int, max: Int): Int = {
        if(min==max)
            return min

        if (min > max)
            return Math.abs(Random.nextInt) % (min - max + 1) + max

        Math.abs(Random.nextInt) % (max - min + 1) + min
    }

    override def random(min: Long, max: Long): Long = {
        if(min==max)
            return min

        if (min > max)
            return Math.abs(Random.nextInt) % (min - max + 1) + max

        Math.abs(Random.nextInt) % (max - min + 1) + min
    }

    override def uuid(): String = {
        UUID.randomUUID.toString
    }
}
