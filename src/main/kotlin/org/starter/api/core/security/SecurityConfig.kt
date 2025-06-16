package org.starter.api.core.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.starter.api.core.AppProperties
import org.starter.api.core.error.ForbiddenException
import org.starter.api.core.log.StructuredLogger

/**
 * Security config
 *
 * @property tokenProvider
 * @constructor Create empty Security config
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val tokenProvider: TokenProvider,
    private val appProperties: AppProperties
) {
    /**
     * 시큐리트 환경 설정 및 경로 설정.
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http.csrf{ obj: CsrfConfigurer<HttpSecurity> -> obj.disable() } // token 방식 이기 떄문에 csrf는 disabled
            .cors { corsCustomizer: CorsConfigurer<HttpSecurity> ->
                corsCustomizer.configurationSource(
                    corsConfigurationSource()
                )
            }
            .sessionManagement { sessionManagement: SessionManagementConfigurer<HttpSecurity?>? ->
                sessionManagement?.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            } // sessonPlicy
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        *arrayOf(
                            AntPathRequestMatcher("/user/login"),
                            AntPathRequestMatcher("/user/refresh"),
                            AntPathRequestMatcher("/v3/api-docs/**"),
                            AntPathRequestMatcher("/webjars/**"),
                            AntPathRequestMatcher("/swagger-resources/**"),
                            AntPathRequestMatcher("/swagger-ui/**"),
                            AntPathRequestMatcher("/swagger/**"),
                            AntPathRequestMatcher("/swagger-ui.html"),
                            AntPathRequestMatcher("/error"),
                            AntPathRequestMatcher("/sse/upload/**"),
                        )
                    )
                    .permitAll()
                    .requestMatchers(AntPathRequestMatcher("/**"))
                    .authenticated()
            } //                        .requestMatchers(new AntPathRequestMatcher("/**"))
            //                        .access(new AuthorizationChackProvider()))

            .addFilterBefore(JwtTokenFilter(tokenProvider, appProperties), UsernamePasswordAuthenticationFilter::class.java)

            .exceptionHandling { exceptionHandler ->
                exceptionHandler
                    .authenticationEntryPoint { request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException ->
                        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)

                        var rootCause = authException
                        if (authException.cause is UsernameNotFoundException) {
                            rootCause = authException.cause as UsernameNotFoundException
                        }
                        StructuredLogger.error(
                            "UNAUTHORIZED",
                            rootCause,
                            request.getParameterMap()
                        )
                    }
                    .accessDeniedHandler{ request: HttpServletRequest, response: HttpServletResponse, accessDeniedException: AccessDeniedException? ->
                        // 필요한 권한이 없이 접근하려 할때 403
                        response.status = HttpServletResponse.SC_FORBIDDEN
                        StructuredLogger.error(
                            "FORBIDDEN",
                            ForbiddenException("권한이 존재하지 않습니다: " + accessDeniedException!!.message),
                            request.getParameterMap()
                        )
                    }
            }
        return http.build()
    }

    /**
     * security cors 허용.
     *
     * @return
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()

        config.allowCredentials = true
        config.allowedOriginPatterns = mutableListOf<String?>("*") //모든 요청을 받는다.
        config.allowedMethods = mutableListOf<String?>("POST", "GET")
        config.allowedHeaders = mutableListOf<String?>("*")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    /**
     * 패스워드 암호화 encoding
     *
     * @return
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}
