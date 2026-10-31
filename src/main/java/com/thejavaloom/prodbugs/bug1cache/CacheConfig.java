package com.thejavaloom.prodbugs.bug1cache;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class CacheConfig {

    /** What you get by default: a cache that lives in THIS JVM's memory. Every instance has its own. */
    @Bean
    @Primary
    CacheManager localCacheManager() {
        return new ConcurrentMapCacheManager();
    }

    /**
     * One cache shared by every instance (Redis, or Memurai on Windows), with a TTL so a missed
     * eviction cannot live forever. The app only connects when the fixed endpoints are called,
     * so everything else runs without Redis.
     */
    @Bean
    CacheManager sharedCacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .build();
    }
}
