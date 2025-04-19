package com.example.shoppingcartservice.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Map<Long, Integer>> cartRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Map<Long, Integer>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        // 定义目标类型 Map<Long, Integer>
        JavaType javaType = TypeFactory.defaultInstance().constructMapType(Map.class, Long.class, Integer.class);

        // 构建安全的 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        // 使用新版构造方法传入 ObjectMapper + 泛型类型
        Jackson2JsonRedisSerializer<Map<Long, Integer>> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, javaType);
//        Jackson2JsonRedisSerializer<Object> serializer =
//                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 设置 key 和 value 的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}