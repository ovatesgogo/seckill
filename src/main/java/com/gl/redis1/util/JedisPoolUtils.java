package com.gl.redis1.util;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
 * 连接池工具类
 */
public class JedisPoolUtils {
    private static volatile JedisPool jedisPool = null;

    private JedisPoolUtils() {
    }

    /**
     * 双重检查锁
     * @return
     */
    public static JedisPool getJedisPool(){
        if (null == jedisPool){
            synchronized (JedisPoolUtils.class){
                if (null==jedisPool){
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    jedisPoolConfig.setMaxTotal(200);
                    jedisPoolConfig.setMaxIdle(32);
                    jedisPoolConfig.setMaxWaitMillis(100*1000);
                    jedisPoolConfig.setBlockWhenExhausted(true);
                    jedisPoolConfig.setTestOnBorrow(true);

                    jedisPool = new JedisPool(jedisPoolConfig,"192.168.174.100", 6379,6000);
                }
            }
        }
        return jedisPool;
    }

    /**
     * 关闭连接
     * @param pool
     * @param jedis
     */
    public static void release(JedisPool pool, Jedis jedis){
        if (null !=jedis){
            jedisPool.returnResource(jedis);
        }
    }

}