package com.skytakeout;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest

public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Test
    public void testRedisTemplate() {
        System.out.println(redisTemplate);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
    }

    @Test
    public void testString() {
        //set
        redisTemplate.opsForValue().set("city", "Peking");
        //get
        String city = (String) redisTemplate.opsForValue().get("city");
        assertThat(city).isEqualTo("Peking");
        //setEX
        redisTemplate.opsForValue().set("code", "114514", 3L, TimeUnit.MINUTES);
        String code = (String) redisTemplate.opsForValue().get("code");
        assertThat(code).isEqualTo("114514");
        //setNX
        Boolean setIfAbsent1 = redisTemplate.opsForValue().setIfAbsent("lock", "1");
        Boolean setIfAbsent2 = redisTemplate.opsForValue().setIfAbsent("lock", "2");

        assertThat(setIfAbsent1).isTrue(); // 第一次应该成功
        assertThat(setIfAbsent2).isFalse(); // 第二次应该失败

        // 验证“lock”的值不应被覆盖
        String lockValue = (String) redisTemplate.opsForValue().get("lock");
        assertThat(lockValue).isEqualTo("1");
    }

    @Test
    public void testHash() {
        //hset
        redisTemplate.opsForHash().put("100","name", "Tom");
        redisTemplate.opsForHash().put("100","age", "20");
        //hget
        String name = (String) redisTemplate.opsForHash().get("100", "name");
        assertThat(name).isEqualTo("Tom");
        //hkeys
        Set<Object> keys = redisTemplate.opsForHash().keys("100");
        assertThat(keys).containsExactlyInAnyOrder("name", "age");
        //hvals
        List<Object> values = redisTemplate.opsForHash().values("100");
        assertThat(values).containsExactlyInAnyOrder("Tom", "20");

        // 清理测试数据
        redisTemplate.delete("100");
        assertThat(redisTemplate.hasKey("100")).isFalse();
    }
}
