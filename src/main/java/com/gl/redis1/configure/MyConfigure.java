package com.gl.redis1.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration()
public class MyConfigure {

//    @Bean
    public Jedis jedis(){
        Jedis jedis = new Jedis("192.168.174.100", 6379);
        return jedis;
    }
}
