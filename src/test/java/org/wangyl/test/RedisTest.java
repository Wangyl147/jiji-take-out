package org.wangyl.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.wangyl.reggie.ReggieApplication;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


//需要遵从spring约定大于配置的习惯，要么测试类和启动类位于同一目录，要么测试类规定启动类
@SpringBootTest(classes = ReggieApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testString() {
        redisTemplate.opsForValue().set("aaaa","beijing");
        redisTemplate.opsForValue().set("dfef","efgderg",10l, TimeUnit.SECONDS);
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("aaaa", "defers");
        System.out.println(aBoolean);
    }

    @Test
    public void testHash() {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put("001","name","dfgers");
        hashOperations.put("001","age","114");
        hashOperations.put("001","aswdeswa","ijoukmhu");

        Object age = hashOperations.get("001", "age");
        System.out.println(age);

        Set keys = hashOperations.keys("001");
        for(Object s:keys) System.out.println(s);

        List values = hashOperations.values("001");
    }

    @Test
    public void testList(){
        SetOperations setOperations=redisTemplate.opsForSet();

    }
}
