package org.starter.api.core.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.starter.api.app.user.domain.User
import org.starter.api.app.user.infra.UserDetailsAdapter

/**
 * Security helper
 *
 * @constructor Create empty Security helper
 */
object SecurityHelper {
    /**
     * 시큐리티 콘텍스트를 가져온다.
     *
     * @return
     */
    val context: SecurityContext = SecurityContextHolder.getContext()

    val loginedUser: User?
        /**
         * 시큐리티 컨텍스트에 저장된 로그인 유저를 가져온다.
         *
         * @return
         */
        get() {
            val authentication: Authentication? =
                context.authentication
            if (authentication == null) {
                return null
            }
            val principal = authentication.principal
            if (principal is UserDetailsAdapter) {
                return principal.getUser()
            } else {
                return null
            }
        }

    /**
     * 로그인 여부를 판별한다.
     *
     * @return
     */
    fun isLogined(): Boolean = loginedUser != null && loginedUser?.isNew == false
}
