package org.starter.api.app.user.infra

import org.starter.api.app.user.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsAdapter(
    private val user: User
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))

    override fun getPassword(): String = "" // 비밀번호 없음 (JWT 기반)

    override fun getUsername(): String = user.email

    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true

    fun getId(): Long = user.id ?: 0L
    fun getRole(): String = user.role.name

    fun getUser() = user
}