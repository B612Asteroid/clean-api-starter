package org.starter.api.core.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.nio.charset.StandardCharsets

@Configuration
class MessageSourceConfig {

    @Bean
    fun messageSource(): MessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasename("messages.message")
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name())
        messageSource.setUseCodeAsDefaultMessage(true)  // 메시지 없을 시 key 그대로 반환
        return messageSource
    }
}