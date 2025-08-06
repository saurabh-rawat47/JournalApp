package net.engineeringdigest.journalApp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.api.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {


    @Autowired
    private RedisTemplate redisTemplate;

//    public <T> T get(String key, Class<T> entityClass) {
//        try {
//            Object o = redisTemplate.opsForValue().get(key);
//            ObjectMapper mapper = new ObjectMapper();
//            return mapper.readValue(o.toString(), entityClass);
//        } catch (Exception e) {
//            log.error("Exception", e);
//            return null;
//        }
//
//    }

    public <T> T get(String key, Class<T> entityClass) {

        Object raw = redisTemplate.opsForValue().get(key);

        if (raw == null) {
            log.info("Cache miss for key: {}", key);
            return null;
            // or throw new CacheMissException(key), or return Optional<T>
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(raw.toString(), entityClass);
        } catch (IOException e) {
            log.error("Failed to parse JSON from Redis for key {}", key, e);
            throw new IllegalStateException("Corrupt cache data for " + key, e);
        }
    }

    public void set(String key, Object o, Long ttl) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, jsonValue, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Exception", e);
        }


    }
}