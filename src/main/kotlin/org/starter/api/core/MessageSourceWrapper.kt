package org.starter.api.core

import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.stereotype.Component
import java.util.Locale
import kotlin.emptyArray

@Component
class MessageSourceWrapper(
    private val messageSource: MessageSource
) {

    /**
     * Get
     * 메시지를 가져온다.
     * @param key
     * @param args
     * @param locale
     * @return
     */
    fun get(key: String, vararg args: Any?, locale: Locale = Locale.getDefault()): String {
        return messageSource.getMessage(key, args, locale)
    }

    /**
     * key에 해당하는 메시지를 가져온다.
     * Locale이 강제된다.
     * @param key
     * @return
     */
    fun get(key: String): String {
        return get(key, emptyArray<String>())
    }

    fun getWithDefault(key: String, default: String, locale: Locale = Locale.getDefault()): String {
        return try {
            get(key, locale = locale)
        } catch (e: NoSuchMessageException) {
            default
        }
    }
}