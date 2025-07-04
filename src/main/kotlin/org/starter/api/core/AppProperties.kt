package org.starter.api.core

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
class AppProperties {
    val jwt = Jwt()
    val redis = Redis()
    val s3 = S3()
    val path = Path()

    class Jwt {
        lateinit var secret: String
        var expireSeconds: Long = 0
        lateinit var authorizationHeader: String
    }

    class Redis {
        lateinit var streamKey: String
        lateinit var group: String
    }

    class S3 {
        lateinit var accessKey: String
        lateinit var secretKey: String
        lateinit var endpoint: String
        lateinit var region: String
        lateinit var bucket: String
    }

    class Path {
        lateinit var root: String
    }
}