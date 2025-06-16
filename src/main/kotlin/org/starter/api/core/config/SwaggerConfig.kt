package org.starter.api.core.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition

@OpenAPIDefinition(info = Info(title = "API 명세서", description = "API 명세서", version = "0.1"))
@org.springframework.context.annotation.Configuration
class SwaggerConfig {
    @org.springframework.context.annotation.Bean
    fun openAPI(): OpenAPI {
        val securityScheme: SecurityScheme? = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name(SystemPropertyHelper.getProperty("jwt.headerKey"))

        val securityRequirement: SecurityRequirement? = SecurityRequirement().addList("bearerAuth")

        val dev: Server = Server()
        dev.setUrl("http://223.130.134.2/api")
        dev.setDescription("Dev Server URL")

        val prod: Server = Server()
        prod.setUrl("https://lcms2.aitextbook.co.kr/api")
        prod.setDescription("LCMS Prod URL")

        val profile = java.lang.System.getProperty("spring.profiles.active")

        return OpenAPI()
            .components(Components().addSecuritySchemes("bearerAuth", securityScheme))
            .info(
                Info()
                    .title("외부 전용 API")
                    .version("1.0")
            )
            .addServersItem(if ("prod" == profile) prod else dev)
            .addTagsItem(Tag().name("ExternalController").description("외부 공유 용 컨트롤러"))
            .security(kotlin.collections.mutableListOf<T?>(securityRequirement))
    }

    @org.springframework.context.annotation.Bean
    fun myControllerApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("external-controller-group") // 그룹 이름 설정
            .packagesToScan("kr.co.chunjae.aidtlcms.external")
            .pathsToMatch("/external/**") // 특정 경로만 매칭
            .build()
    }

    @org.springframework.context.annotation.Bean
    fun forwardedHeaderFilter(): ForwardedHeaderFilter {
        return ForwardedHeaderFilter()
    }
    // 완료가 되었으면 오른쪽 URL 로 접속 => http://localhost:8081/swagger-ui/index.html
}
