package org.starter.api.core

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
class AppPropertiesTest @Autowired constructor(
    val appProperties: AppProperties
) {

    @Test
    fun `JWT 설정이 주입된다`() {
        assertEquals("test-secret", appProperties.jwt.secret)
        assertEquals(9999, appProperties.jwt.expireSeconds)
    }

    @Test
    fun `Redis 설정이 주입된다`() {
        assertEquals("test-stream", appProperties.redis.streamKey)
        assertEquals("test-group", appProperties.redis.group)
    }
}