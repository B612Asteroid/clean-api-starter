package org.starter.api.core.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.starter.api.core.error.InvalidTokenException
import java.io.IOException

class JwtTokenFilter(private val tokenProvider: TokenProvider) : OncePerRequestFilter() {
    /**
     * 필터 적용
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        servletRequest: HttpServletRequest,
        servletResponse: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwtToken = resolveToken(servletRequest)
        try {
            if (jwtToken != null) {
                tokenProvider.validateToken(jwtToken)
                val authentication: Authentication? = tokenProvider.getAuthentication(jwtToken)
                log.debug("authentication ==> {}", authentication)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: InvalidTokenException) {
            log.error(e.message, e)
        } finally {
            filterChain.doFilter(servletRequest, servletResponse)
        }
    }


    /**
     * Request Header 에서 토큰 정보를 꺼낸다
     *
     * @param request
     * @return
     */
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken: String? = request.getHeader(AUTHORIZATION_HEADER)

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        } else if (bearerToken != null) {
            return bearerToken
        }

        return null
    }


    companion object {
        val AUTHORIZATION_HEADER: String? = "jwt.headerKey"
        private val log: Logger = LoggerFactory.getLogger(JwtTokenFilter::class.java)
    }
}
