package com.hmdp;

import com.hmdp.utils.RedisConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class HmDianPingApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Test
    void TestRedis() {
        System.out.println("Starting TestRedis");
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + "13198222888", "code");
        stringRedisTemplate.expire(RedisConstants.LOGIN_CODE_KEY + "13198222888", 30, TimeUnit.SECONDS);
        String value = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + "13198222888");
        System.out.println(value);
    }

}
