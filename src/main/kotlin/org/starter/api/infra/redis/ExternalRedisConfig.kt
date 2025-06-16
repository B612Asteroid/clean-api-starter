package org.starter.api.infra.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * 테스트용으로만 사용
 * 로컬 환경일 때 내부 레디스 실행
 */
@Configuration
@Profile("!test")
class ExternalRedisConfig {

    @Value("\${spring.data.redis.host}")
    private val redisHost: String = ""

    @Value("\${spring.data.redis.port}")
    private val redisPort = 0

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(redisHost, redisPort)
    }

    @Bean
    @Primary
    fun rawTypeRedisTemplate(): RedisTemplate<*, *> {
        val redisTemplate = RedisTemplate<String, String>()
        val genericJackson2JsonRedisSerializer =
            GenericJackson2JsonRedisSerializer()
        redisTemplate.connectionFactory = redisConnectionFactory()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = genericJackson2JsonRedisSerializer
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = genericJackson2JsonRedisSerializer

        redisTemplate.afterPropertiesSet()
        return redisTemplate
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        val genericJackson2JsonRedisSerializer =
            GenericJackson2JsonRedisSerializer()
        redisTemplate.connectionFactory = redisConnectionFactory()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = genericJackson2JsonRedisSerializer
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = genericJackson2JsonRedisSerializer

        redisTemplate.afterPropertiesSet()
        return redisTemplate
    }
}
