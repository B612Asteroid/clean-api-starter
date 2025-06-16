package org.starter.api.core.security

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import kr.co.chunjae.aidtlcms.user.token.Token
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.starter.api.core.error.InvalidTokenException
import java.security.Key
import java.time.ZonedDateTime
import java.util.*

/**
 * 토큰 생성 / 조회 클래스
 *
 * @property userDetailsService
 * @constructor Create empty Token provider
 */
@Component
class TokenProvider(
    private val userDetailsService: UserDetailsService,
) : InitializingBean {
    private val logger: Logger = LoggerFactory.getLogger(TokenProvider::class.java)
    private val EXPIRE_TIME: Long = 12000
    private val REFRESH_TIME: Long = 24000
    private val SECRET_KEY = "jwt.secretKey"
    private var key: Key? = null

    /**
     * 빈이 생성되고 주입을 받은 후에 secret값을 Base64 Decode해서 key 변수에 할당.
     *
     * @throws Exception
     */
    override fun afterPropertiesSet() {
        val keyBytes: ByteArray? = Decoders.BASE64.decode(SECRET_KEY)
        this.key = Keys.hmacShaKeyFor(keyBytes)
    }

    /**
     * 토큰발급
     *
     * @param userId
     * @param role
     * @return
     */
    fun createAccessToken(userId: String?, role: String?): Token? {
        val claims: Claims = Jwts.claims().setSubject(userId)
        claims.put("roles", role)
        // 토큰의 expire 시간을 설정
        val now: ZonedDateTime = ZonedDateTime.now()
        val validity: ZonedDateTime = now.plusSeconds(this.EXPIRE_TIME)
        val refreshValidity: ZonedDateTime = now.plusSeconds(this.REFRESH_TIME)

        logger.debug("refresh Token date ==> {}, {}", validity, refreshValidity)

        val accessToken: String? = Jwts.builder()
            .setHeaderParam("alg", SignatureAlgorithm.HS512)
            .setClaims(claims)
            .setSubject(userId)
            .setExpiration(Date.from(validity.toInstant()))
            .setIssuedAt(Date.from(now.toInstant()))
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()

        val refreshToken: String? = Jwts.builder()
            .setHeaderParam("alg", SignatureAlgorithm.HS512)
            .setClaims(claims)
            .setSubject(userId)
            .setExpiration(Date.from(refreshValidity.toInstant()))
            .setIssuedAt(Date.from(now.toInstant()))
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()

        return Token("Bearer ", accessToken, refreshToken)
    }

    /**
     * 인증
     *
     * @param token
     * @return
     */
    fun getAuthentication(token: String?): Authentication {
        // TODO 유저 정보 가져올 방법 생각하기
        val userDetails: UserDetails = userDetailsService.loadUserByUsername(this.getUserId(token))
        return UsernamePasswordAuthenticationToken(
            userDetails, userDetails.password,
            userDetails.authorities
        )
    }

    /**
     * JWT 유저 아이디 추출
     *
     * @param token
     * @return
     */
    fun getUserId(token: String?): String {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject()
    }

    /**
     * 토큰 validation 체크
     *
     * @param token
     * @return
     */
    @Throws(InvalidTokenException::class)
    fun validateToken(token: String?) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
        } catch (e: SecurityException) {
            throw InvalidTokenException("잘못된 JWT 서명입니다.")
        } catch (e: MalformedJwtException) {
            throw InvalidTokenException("잘못된 JWT 서명입니다.")
        } catch (e: ExpiredJwtException) {
            logger.info("{}", e.getClaims().getExpiration())
            throw InvalidTokenException("만료된 JWT 토큰입니다.")
        } catch (e: UnsupportedJwtException) {
            throw InvalidTokenException("지원되지 않는 JWT 토큰입니다.")
        } catch (e: IllegalArgumentException) {
            throw InvalidTokenException("토큰 처리 시 내부 에러가 발생했습니다.")
        }
    }

    fun getClaims(token: String?): Claims? {
        val claims: Claims? = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()

        return claims
    }
}
