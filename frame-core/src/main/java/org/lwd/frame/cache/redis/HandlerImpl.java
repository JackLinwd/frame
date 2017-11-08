package org.lwd.frame.cache.redis;

import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.cache.Handler;
import org.lwd.frame.util.Serializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.inject.Inject;

/**
 * @author lwd
 */
@Component("frame.cache.redis.handler")
public class HandlerImpl implements Handler, ContextRefreshedListener {
    @Inject
    private Serializer serializer;
    @Value("${frame.cache.name:}")
    private String name;
    @Value("${frame.cache.redis.host:localhost}")
    private String host;
    @Value("${frame.cache.redis.max-total:500}")
    private int total;
    @Value("${frame.cache.redis.max-idle:5}")
    private int idle;
    @Value("${frame.cache.redis.max-wait:500}")
    private long wait;
    private JedisPool pool;

    @Override
    public String getName() {
        return "redis";
    }

    @Override
    public void put(String key, Object value, boolean resident) {
        Jedis jedis = pool.getResource();
        jedis.set(key.getBytes(), serializer.serialize(value));
        jedis.close();
    }

    @Override
    public <T> T get(String key) {
        return get(key, false);
    }

    @Override
    public <T> T remove(String key) {
        return get(key, true);
    }

    @SuppressWarnings({"unchecked"})
    private <T> T get(String key, boolean remove) {
        Jedis jedis = pool.getResource();
        byte[] k = key.getBytes();
        byte[] bytes = jedis.get(k);
        if (remove)
            jedis.del(k);
        jedis.close();

        return (T) serializer.unserialize(bytes);
    }

    @Override
    public int getContextRefreshedSort() {
        return 6;
    }

    @Override
    public void onContextRefreshed() {
        if (!getName().equals(name))
            return;

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(total);
        config.setMaxIdle(idle);
        config.setMaxWaitMillis(wait);
        config.setTestOnBorrow(true);
        pool = new JedisPool(config, host);
    }
}
