package org.starter.api.core.log

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.starter.api.core.security.SecurityHelper
import java.time.Instant
import java.util.*

@Component
class TraceIdFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val incomingTraceId = request.getHeader("X-Trace-Id")
            val traceId = incomingTraceId ?: UUID.randomUUID().toString().replace("-", "").take(24)
            MDC.put("traceId", traceId)
            response.setHeader("X-Trace-Id", traceId)
            val userId = SecurityHelper.loginUserId
            if (userId != null) {
                MDC.put("userId", userId)
            }

            if (isApiInfoLogging(request)) {
                // 🔥 API Info 로그
                val logData = mapOf(
                    "event" to "API_CALL",
                    "method" to request.method,
                    "uri" to request.requestURI,
                    "traceId" to traceId,
                    "userId" to MDC.get("userId"),
                    "timestamp" to Instant.now().toString()
                )
                LOG.info(logData.toString())
            }

            filterChain.doFilter(request, response)
        } finally {
            MDC.clear()
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(TraceIdFilter::class.java)

        private val API_LOG_WHITELIST = listOf("/contents", "/node", "/exam")

        private fun isApiInfoLogging(request: HttpServletRequest): Boolean {
            val hasUser = MDC.get("userId") != null
            val isWhitelisted = API_LOG_WHITELIST.any { prefix -> request.requestURI.startsWith(prefix) }
                    && request.method == "POST"
            return hasUser && isWhitelisted
        }
    }
}