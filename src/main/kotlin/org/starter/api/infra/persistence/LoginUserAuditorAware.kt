package org.starter.api.infra.persistence

import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.starter.api.core.security.SecurityHelper
import java.util.*

@Configuration
@EnableJpaAuditing
class LoginUserAuditorAware : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> =
        Optional.ofNullable(SecurityHelper.loginedUser?.userId ?: "")
}
