package org.starter.api.infra.s3

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.starter.api.core.AppProperties

/**
 * Amazon S3에 연결하는 클라이언트를 생성한다.
 */
@Configuration
class S3ClientProvider(
    private val appProperties: AppProperties
) {
    private val accessKey: String = appProperties.s3.accessKey
    private val secretKey: String = appProperties.s3.secretKey
    private val endPoint: String = appProperties.s3.endpoint
    private val regionName: String = appProperties.s3.region

    @get:Bean
    val connection: AmazonS3
        /**
         * AmazonS3에 접속한 커넥션을 받아온다.
         *
         * @return
         */
        get() = AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .build()
}
