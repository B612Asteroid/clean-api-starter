package org.starter.api.core.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.starter.api.infra.persistence.Persistable
import java.time.Instant

object StructuredLogger {

    private val log: Logger = LoggerFactory.getLogger(StructuredLogger::class.java)

    /**
     * 맵을 logger.info 형태로 남긴다.
     *
     * @param event
     * @param domain
     * @param data
     */
    fun info(
        event: String,
        domain: Class<out Persistable>,
        data: Map<String, Any?> = emptyMap()
    ) {

        log.info("{}", baseStructuredLogger(event, domain, data))
    }

    /**
     * 필요한 맵을 logger.warn 형태로 남긴다.
     *
     * @param event
     * @param domain
     * @param data
     */
    fun warn(
        event: String,
        domain: Class<out Persistable>,
        data: Map<String, Any?> = emptyMap()
    ) {

        log.warn("{}", baseStructuredLogger(event, domain, data))
    }

    /**
     * 필요한 이벤트를 log.error 로 남긴다.
     *
     * @param event
     * @param exception
     * @param data
     */
    fun error(
        event: String,
        exception: Throwable,
        data: Map<String, Any?> = emptyMap()
    ) {
        val base = mutableMapOf<String, Any?>(
            "event" to event,
            "traceId" to MDC.get("traceId"),
            "userId" to MDC.get("userId"),
            "timestamp" to Instant.now().toString(),
            "errorType" to exception.javaClass.simpleName,
            "message" to exception.message
        )
        base["stacktrace"] = extractRootError(exception)
        base.putAll(data)
        if (isDev()) {
            log.error("{}", exception)
        } else {
            log.error("{}", base)
        }

    }


    ////////////////////////////////////////// private

    private fun isDev(): Boolean = System.getProperty("spring.profiles.active") != "prod"

    fun extractRootError(exception: Throwable): String {
        val root = getRootCause(exception)
        val location = root.stackTrace.firstOrNull()?.let {
            "${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
        } ?: "unknown"
        return "${root.javaClass.simpleName}: ${root.message} @ $location"
    }

    fun getRootCause(e: Throwable): Throwable {
        var cause: Throwable? = e
        while (cause?.cause != null && cause != cause.cause) {
            cause = cause.cause
        }
        return cause ?: e
    }

    /**
     * (non-javadoc)
     * event를 받아, 남긴다.
     * # TODO event ENUM으로 받게 변경
     *
     * @param event
     * @param domain
     * @param data
     * @return
     */
    private fun baseStructuredLogger(
        event: String,
        domain: Class<out Persistable>,
        data: Map<String, Any?> = emptyMap()
    ): Map<String, Any?> {
        val base = mutableMapOf<String, Any?>(
            "event" to event,
            "domain" to domain.simpleName,
            "traceId" to MDC.get("traceId"),
            "userId" to MDC.get("userId"),
            "timestamp" to Instant.now().toString()
        )
        base.putAll(data)
        return base
    }
}