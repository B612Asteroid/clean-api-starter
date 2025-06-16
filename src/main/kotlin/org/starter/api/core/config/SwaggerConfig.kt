package org.starter.api.core.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.ForwardedHeaderFilter

@OpenAPIDefinition(info = Info(title = "API 명세서", description = "API 명세서", version = "0.1"))
@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI {
        val securityScheme: SecurityScheme? = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name("jwt.headerKey")

        val securityRequirement: SecurityRequirement? = SecurityRequirement().addList("bearerAuth")

        val profile = System.getProperty("spring.profiles.active")

        val dev = Server()
        dev.url = "dev-server"
        dev.description = "Dev Server"

        val prod = Server()
        prod.url = "prod-server"
        prod.description = "Prod Server"

        return OpenAPI()
            .components(Components().addSecuritySchemes("bearerAuth", securityScheme))
            .info(
                io.swagger.v3.oas.models.info.Info()
                    .title("외부 전용 API")
                    .version("1.0")
            )
            .addServersItem(if ("prod" == profile) prod else dev)
            .security(listOf(securityRequirement))
    }

    @Bean
    fun forwardedHeaderFilter(): ForwardedHeaderFilter {
        return ForwardedHeaderFilter()
    }
    // 완료가 되었으면 오른쪽 URL 로 접속 => http://localhost:8081/swagger-ui/index.html
}
