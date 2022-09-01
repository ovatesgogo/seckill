package com.gl.redis1.controller;

import com.gl.redis1.util.JedisPoolUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Random;

@Controller
public class JController {

    @ResponseBody
    @RequestMapping("/redis")
    public String get() {
        String uid = randUid();
        SecKill(uid, "01");
        return "kdksd";
    }

    public void SecKill(String uid, String pid) {
        JedisPool jedisPool = JedisPoolUtils.getJedisPool();
        Jedis jedis = jedisPool.getResource();
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(pid)) {
            return;
        }
//        jedis.watch(pidKey);
//            if (jedis.get(pidKey)==null){
//                System.out.println("秒杀活动还没开始");
//                return;
//            }
//            if (!StringUtils.isEmpty(jedis.get(pidKey))){
//                int value = Integer.parseInt(jedis.get(pidKey));
//                if (value<=0){
//                    System.out.println("秒杀活动已经结束");
//                    return;
//                }
//        }
//
//        if (jedis.sismember("uid",uid)){
//            System.out.println("你已经秒杀过了");
//            return;
//        }
//        Transaction multi = jedis.multi();
//
//        multi.decr(pidKey);
//        multi.sadd(uidKey,uid);
//        List<Object> result = multi.exec();
//        if (result==null){
//            System.out.println("秒杀失败");
//            jedis.close();
//            return;
//        }
//        System.out.println("秒杀成功");
//        jedis.close();
//    }
//
        /**
         * 加入脚本解决库存遗留问题将复杂的或者多步的redis操作，写为一个脚本，一次提交给redis执行，减少反复连接redis的次数。提升性能。
         * LUA脚本是类似redis事务，有一定的原子性，不会被其他命令插队，可以完成一些redis事务性的操作。
         */
       String script ="local userid=KEYS[1]; \n" +
               "local prodid=KEYS[2];\n" +
               "local qtkey=\"sk:\"..prodid..\":qt\";\n" +
               "local usersKey=\"sk:\"..userid..\":usr\"; \n" +
               "local userExists=redis.call(\"sismember\",usersKey,userid);\n" +
               "if tonumber(userExists)==1 then \n" +
               "  return 2;\n" +
               "end\n" +
               "local num= redis.call(\"get\" ,qtkey);\n" +
               "if tonumber(num)<=0 then \n" +
               "  return 0; \n" +
               "else \n" +
               "  redis.call(\"decr\",qtkey);\n" +
               "  redis.call(\"sadd\",usersKey,userid);\n" +
               "end\n" +
               "return 1;";
        String shal = jedis.scriptLoad(script);
        Object evalsha = jedis.evalsha(shal, 2, uid, pid);
        String result = String.valueOf(evalsha);
        if ("0".equals(result)) {
            System.out.println("已经抢光了");
        } else if ("1".equals(result)) {
            System.out.println("抢购成功");
        } else if ("2".equals(result)) {
            System.out.println("该用户已经抢购过了");
        } else {
            System.out.println("抢购异常");
        }
        jedis.close();
    }

    public String randUid() {
        String uid = "";
        for (int i = 0; i < 7; i++) {
            Random random = new Random();
            int i1 = random.nextInt(10);
            uid = i1 + uid;
        }
        return uid;
    }
}
