package org.starter.api.app.file.ui

import com.amazonaws.services.s3.model.AmazonS3Exception
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration
import com.amazonaws.services.s3.model.CORSRule
import com.amazonaws.services.s3.model.GroupGrantee
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest
import com.amazonaws.services.s3.model.Permission
import io.swagger.v3.oas.annotations.Operation
import org.starter.api.app.file.service.AttachmentService
import org.starter.api.core.AppProperties
import org.starter.api.core.ui.BaseController
import org.starter.api.core.ui.response.ResponseObject
import org.starter.api.infra.s3.S3ClientProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.starter.api.core.error.PersistenceException
import java.util.ArrayList

/**
 * 업로드 파일 용 컨트롤러
 * 아마존 S3 파일을 여기에 담아오는 데 사용된다.
 */
@RestController
@RequestMapping("/file/upload")
class AttachmentController(
    private val s3ClientProvider: S3ClientProvider,
    private val attachmentService: AttachmentService,
    private val appProperties: AppProperties
) : BaseController() {


    /**
     * cors를 설정한다. 스웨거에선 드러나선 안된다.
     *
     * @param targetUrl
     * @return
     */
    @PostMapping("/cors")
    fun saveCor(targetUrl: String?): ResponseEntity<ResponseObject> {
        val s3Client = s3ClientProvider.connection
        val bucketName = appProperties.s3.bucket
        // Create CORS rules.
        val methodRule: MutableList<CORSRule.AllowedMethods?> = ArrayList<CORSRule.AllowedMethods?>()
        methodRule.add(CORSRule.AllowedMethods.GET)
        methodRule.add(CORSRule.AllowedMethods.POST)
        val rule = CORSRule().withId("CORSRule")
            .withAllowedMethods(methodRule)
            .withAllowedHeaders(mutableListOf<String?>("*"))
            .withAllowedOrigins(mutableListOf<String?>("*"))
            .withExposedHeaders(
                mutableListOf<String?>(
                    "ETag", "x-amz-meta-custom-header", "x-amz-server-side-encryption",
                    "x-amz-request-id", "x-amz-id-2"
                )
            )

        // .withMaxAgeSeconds(3000);
        val rules: MutableList<CORSRule?> = ArrayList<CORSRule?>()
        rules.add(rule)

        // Add rules to new CORS configuration.
        var configuration = BucketCrossOriginConfiguration()
        configuration.rules = rules

        // Set the rules to CORS configuration.
        s3Client.setBucketCrossOriginConfiguration(bucketName, configuration)

        // Get the rules for CORS configuration.
        configuration = s3Client.getBucketCrossOriginConfiguration(bucketName)
        return this.createSuccessResponseJson(configuration)
    }

    /**
     * cors를 설정한다. 스웨거에선 드러나선 안된다.
     *
     * @return
     */
    @PostMapping("/acl")
    fun saveAcl(): ResponseEntity<ResponseObject> {
        val s3Client = s3ClientProvider.connection
        val bucketName = appProperties.s3.bucket
        // set bucket ACL
        try {
            // get the current ACL
            val accessControlList = s3Client.getBucketAcl(bucketName)

            // add read permission to anonymous
            accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read)

            s3Client.setBucketAcl(bucketName, accessControlList)
        } catch (e: AmazonS3Exception) {
            log.error(e.getErrorMessage())
        }
        return this.createSuccessResponseJson(null)
    }

    @get:GetMapping("/multiparts")
    val multiparts: ResponseEntity<ResponseObject>
        get() {
            val s3Client = s3ClientProvider.connection
            val multiparts = s3Client.listMultipartUploads(
                ListMultipartUploadsRequest(appProperties.s3.bucket)
            )

            return this.createSuccessResponseJson(multiparts)
        }

    @Operation(summary = "이미지 다운로드")
    @GetMapping("/download/image")
    @Throws(PersistenceException::class)
    fun imageDownload(@RequestParam url: String): ResponseEntity<ResponseObject> {
        return this
            .createSuccessResponseJson(this.createSuccessResponseJson(attachmentService.imageDownload(url)))
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AttachmentController::class.java)
    }
}