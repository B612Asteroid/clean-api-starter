package org.starter.api.core

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
class AppProperties {
    val jwt = Jwt()
    val redis = Redis()

    class Jwt {
        lateinit var secret: String
        var expireSeconds: Long = 0
        lateinit var authorizationHeader: String
    }

    class Redis {
        lateinit var streamKey: String
        lateinit var group: String
    }
}