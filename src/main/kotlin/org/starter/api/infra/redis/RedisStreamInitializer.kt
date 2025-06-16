package org.starter.api.infra.redis

import io.lettuce.core.RedisBusyException
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.RedisSystemException
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.core.RedisTemplate
import org.starter.api.core.log.StructuredLogger

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Profile("!test")
class RedisStreamInitializer(private val redisTemplate: RedisTemplate<String, Any>) {

    @PostConstruct
    fun init() {
        try {
            log.info("🌱 Redis Stream Group Init Start")
            redisTemplate.opsForStream<String, Any>().createGroup(
                "redis.stream.key",
                ReadOffset.latest(),
                "redis.stream.group"
            )
        } catch (e: RedisSystemException) {
            if (e.cause?.message?.contains("BUSYGROUP") == true) {
                StructuredLogger.error("STREAM_GROUP_INIT_FAIL", e, HashMap())
                throw e
            }
            // #. 이미 있어서 Exception이 나는 경우에는 무시한다.
        } catch (e: RedisBusyException) {
            if (e.cause?.message?.contains("BUSYGROUP") == true) {
                StructuredLogger.error("STREAM_GROUP_INIT_FAIL", e, HashMap())
                throw e
            }
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(RedisStreamInitializer::class.java)
    }
}
