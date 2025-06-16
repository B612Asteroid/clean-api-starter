package org.starter.api.infra.redis

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.nio.charset.StandardCharsets
import java.time.Duration

@EnableCaching
@Configuration
@Profile("dev", "prod", "local")
class RedisCacheConfig {
    @Bean
    fun genericJackson2JsonRedisSerializer(): GenericJackson2JsonRedisSerializer {
        val mapper = com.fasterxml.jackson.databind.ObjectMapper()
        mapper.setDefaultPropertyInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
        mapper.activateDefaultTyping(
            com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator.instance,
            com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
        )
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.registerModule(com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
        return GenericJackson2JsonRedisSerializer(mapper)
    }

    @Bean
    fun redisCacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer {
        return RedisCacheManagerBuilderCustomizer { builder: RedisCacheManager.RedisCacheManagerBuilder ->
            builder
                .withCacheConfiguration("menu", getRedisCacheConfiguration(java.time.Duration.ofHours(12)))
                .withCacheConfiguration("code", getRedisCacheConfiguration(java.time.Duration.ofMinutes(10)))
        }
    }

    @Bean
    fun redisCacheConfiguration(serializer: GenericJackson2JsonRedisSerializer): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith({ cacheName -> "$cacheName::" })
            .entryTtl(java.time.Duration.ofHours(1))
            .disableCachingNullValues()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer(StandardCharsets.UTF_8))
            )
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
    }

    @Bean
    fun cacheManager(
        connectionFactory: RedisConnectionFactory,
        serializer: GenericJackson2JsonRedisSerializer
    ): CacheManager {
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(redisCacheConfiguration(serializer))
            .build()
    }

    private fun getRedisCacheConfiguration(ttl: Duration): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(ttl)
            .disableCachingNullValues()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer())
            )
    }
}