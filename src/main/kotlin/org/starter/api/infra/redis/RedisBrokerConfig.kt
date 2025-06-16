package org.starter.api.infra.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.RedisMessageListenerContainer

/**
 * 레디스 메시지 핸들러 설정
 */
@Configuration
@Profile("!test")
class RedisBrokerConfig {
    /**
     * redis pub/sub 메시지를 처리하는 listener 설정
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    fun redisMessageListener(connectionFactory: RedisConnectionFactory): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        return container
    }
}
