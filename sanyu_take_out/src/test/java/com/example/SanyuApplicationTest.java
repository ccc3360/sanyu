package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class SanyuApplicationTest {

//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//
//    @Test
//    void Ssetget() {
//        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
//
//        HashOperations<String, Object, Object> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
//        stringObjectObjectHashOperations.put("bbb","bb","b");
//    }
}
