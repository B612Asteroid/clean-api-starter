package org.starter.api.app.file.service

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.model.AmazonS3Exception
import org.starter.api.app.file.domain.Attachment
import org.starter.api.app.file.domain.UploaderType
import org.starter.api.app.file.ui.AttachmentDTO
import org.starter.api.core.error.PersistenceException
import org.starter.api.core.log.StructuredLogger
import org.starter.api.core.util.TypeUtil
import org.starter.api.infra.persistence.Persistable
import org.starter.api.infra.persistence.PersistenceService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.function.Consumer

/**
 * 파일 처리 서비스
 */
@Service("attachmentService")
class AttachmentService(
    private val dependencies: AttachmentDependencies,
    private val persistenceService: PersistenceService,
) {
    /**
     * DTO를 통해 DB에 Attachment를 저장한다.
     *
     * @param attachmentDTO
     * @param reference
     * @return
     * @throws PersistenceException
     */
    @Throws(PersistenceException::class)
    fun saveAttachment(attachmentDTO: AttachmentDTO, reference: Persistable): Attachment {
        var attachment = Attachment()
        val id = attachmentDTO.id
        if (id != null) {
            attachment =
                dependencies.attachmentRepository.findById(id).orElseThrow( // #. 식별자에 해당하는 객체가 존재하지 않습니다.
                    { PersistenceException(dependencies.messageSourceWrapper.get("COMMON.ERR03", attachmentDTO.id)) }
                )
        }

        if (TypeUtil.notNull(attachmentDTO.altText)) {
            attachment.altText = TypeUtil.stringValue(attachmentDTO.altText)
        }
        if (TypeUtil.notNull(attachmentDTO.fileType)) {
            attachment.fileType = TypeUtil.getCodeEnum(UploaderType::class.java, attachmentDTO.fileType)
        }
        if (TypeUtil.notNull(attachmentDTO.filePath)) {
            attachment.filePath = attachmentDTO.filePath
        }
        if (TypeUtil.notNull(attachmentDTO.originPath)) {
            attachment.originPath = attachmentDTO.originPath
        }
        if (TypeUtil.notNull(attachmentDTO.name)) {
            attachment.name = attachmentDTO.name
        }
        attachment.orders = TypeUtil.intValue(attachmentDTO.orders, 1)

        attachment.setReference(reference)
        dependencies.self.saveDBUpload(attachment)
        return attachment
    }


    /**
     * 데이터베이스에 Attachment 객체를 넣는다.
     *
     * @param attachment
     * @return
     */
    @Transactional(rollbackFor = [Exception::class])
    fun saveDBUpload(attachment: Attachment): Attachment {
        return dependencies.attachmentRepository.save(attachment)
    }


    /**
     * ID로 업로드를 검색하여 가져온다.
     *
     * @param uploadId
     * @return
     */
    @Throws(PersistenceException::class)
    fun getUpload(uploadId: Long): Attachment {
        log.debug("AttachmentService.getAttachment START")
        // #. 식별자 {0}에 해당하는 업로드 파일 객체가 존재하지 않습니다.
        return dependencies.attachmentRepository.findById(uploadId).orElseThrow({
            PersistenceException(
                dependencies.messageSourceWrapper.get(
                    "UPLOAD.ERR01", uploadId
                )
            )
        })
    }

    /**
     * @param cdnPath
     * @return
     * @throws PersistenceException
     */
    fun listObjects(cdnPath: String): MutableList<String> {
        return dependencies.s3ClientServicePart.listObjects(cdnPath)
    }

    /**
     * S3로부터 파일을 다운로드 받는다.
     *
     * @param filePath
     * @return
     */
    @Throws(PersistenceException::class)
    fun s3Download(filePath: String): ByteArray {
        var filePath = filePath
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1)
        }
        return dependencies.s3ClientServicePart.s3Download(
            dependencies.appProperties.s3.bucket,
            filePath
        )
    }

    /**
     * 객체를 카피한다.
     *
     * @param originAttachment 오리지널 객체
     * @return
     */
    @Throws(PersistenceException::class)
    fun copyUpload(originAttachment: Attachment): Attachment {
        val attachment = originAttachment
        log.debug("UploadService.copyUpload START")

        val originFilePath = attachment.filePath ?: ""
        attachment.markAsNew()
        // #. Attachment.name 에 Copy 붙여주기
        val tempPath: Array<String?> =
            originFilePath.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val ext = tempPath[1]
        val copyName = tempPath[0] + "_copy"
        val copyFilePath = copyName + "." + ext
        this.copyFromS3(originFilePath, copyFilePath)

        // #.데이터베이스에 복사
        attachment.filePath = copyFilePath
        attachment.originPath = copyFilePath
        persistenceService.insert(attachment)

        log.debug("UploadService.copyUpload END")
        return attachment
    }


    /**
     * 업로드 객체의 deleteYn만 Y로 바꾼다. (실제 삭제 X)
     *
     * @param uploadId
     * @return
     */
    @Transactional(rollbackFor = [Exception::class])
    fun deleteAttachment(uploadId: Long) {
        val attachment: Attachment? = persistenceService.find(Attachment::class.java, uploadId)
        if (attachment != null) {
            attachment.deleteYn = true
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun deleteAttachments(uploadIds: MutableList<Long>) {
        uploadIds.forEach(Consumer { uploadId: Long -> this.deleteAttachment(uploadId) })
    }


    /**
     * Amazon S3에 파일 업로드
     *
     * @param file
     * @param fname
     * @return
     */
    fun s3Upload(file: MultipartFile, fname: String): Int {
        return this.s3Upload(file, fname, file.contentType ?: "application/octet-stream")
    }

    /**
     * Amazon S3에 파일 업로드 오버로딩
     *
     * @param file
     * @param fileName
     * @return
     */
    fun s3Upload(file: MultipartFile, fileName: String, contentType: String): Int {
        try {
            dependencies.s3ClientServicePart.s3Upload(file, fileName, contentType)
            return 1
        } catch (e: IOException) {
            return -1
        } catch (e: InterruptedException) {
            return -1
        } catch (e: AmazonClientException) {
            return -1
        }
    }

    /**
     * 파일을 다른 파일로 복사한다.
     *
     * @param orgKey
     * @param copyKey
     */
    @Throws(PersistenceException::class)
    fun copyFromS3(orgKey: String, copyKey: String) {
        try {
            dependencies.s3ClientServicePart.copyFromS3(orgKey, copyKey)
        } catch (e: AmazonServiceException) {
            throw PersistenceException(e)
        }
    }

    /**
     * 파일을 삭제한다.
     *
     * @param key
     */
    fun deleteFromS3(key: String?) {
        dependencies.s3ClientServicePart.deleteS3File(key)
    }


    /**
     * 이미지를 다운로드한다.
     *
     * @param url
     * @return
     * @throws PersistenceException
     */
    @Transactional(rollbackFor = [Exception::class])
    @Throws(PersistenceException::class)
    fun imageDownload(url: String): ResponseEntity<ByteArray?> {
        val bytes: ByteArray? = dependencies.self.s3Download(url)
        val httpHeaders = HttpHeaders()
        httpHeaders.setContentType(MediaType.IMAGE_PNG)
        httpHeaders.setContentDispositionFormData("attachment", "image")

        return ResponseEntity<ByteArray?>(bytes, httpHeaders, HttpStatus.OK)
    }

    /**
     * S3에 파일이 있는지 체크하여 true/false로 return
     *
     * @param filePath
     * @return
     */
    fun isExistFile(filePath: String): Boolean {
        try {
            return dependencies.s3ClientServicePart.isExistFile(filePath)
        } catch (e: AmazonS3Exception) {
            if (e.statusCode == 404) {
                return false // 존재하지 않음
            }
            StructuredLogger.error(
                "S3_SDK_ERROR", e, mapOf(
                    "filePath" to filePath,
                    "statusCode" to e.statusCode
                )
            )
            return false
        } catch (e: SdkClientException) {
            StructuredLogger.error("S3_CLIENT_ERROR", e, mapOf("filePath" to filePath))
            return false
        }
    }

    /**
     * S3 폴더 복사(메소드 오버로딩)
     *
     * @param sourceKey
     * @param targetKey
     * @throws PersistenceException
     */
    @Throws(PersistenceException::class)
    fun copyFromS3Directory(sourceKey: String, targetKey: String) {
        val bucketName: String? = dependencies.appProperties.s3.bucket
        this.copyFromS3Directory(bucketName, sourceKey, targetKey)
    }

    /**
     * S3 폴더 복사
     *
     * @param bucketName // 대상 버킷 이름
     * @param sourceKey  // 대상 키
     */
    @Throws(PersistenceException::class)
    fun copyFromS3Directory(bucketName: String?, sourceKey: String, targetKey: String) {
        try {
            dependencies.s3ClientServicePart.copyFromS3Directory(bucketName, sourceKey, targetKey)
        } catch (e: AmazonS3Exception) {
            StructuredLogger.error(
                "S3_SDK_ERROR", e, mapOf(
                    "filePath" to sourceKey,
                    "statusCode" to e.statusCode
                )
            )
            throw e
        } catch (e: SdkClientException) {
            StructuredLogger.error("S3_CLIENT_ERROR", e, mapOf("filePath" to sourceKey))
            throw e
        }
    }

    /**
     * 파일을 이동한다.
     *
     * @param sourceKey
     * @param targetKey
     */
    @Throws(PersistenceException::class)
    fun moveS3Directory(sourceKey: String, targetKey: String) {
        val bucketName: String = dependencies.appProperties.s3.bucket
        this.moveS3Directory(bucketName, sourceKey, targetKey)
    }

    /**
     * 파일을 이동한다.
     * 기본적으로 S3는 이동이라는 개념이 존재하지 않기 때문에 복사 후 삭제하는 방법을 사용한다.
     *
     * @param bucketName
     * @param sourceKey
     * @param targetKey
     */
    @Throws(PersistenceException::class)
    fun moveS3Directory(bucketName: String, sourceKey: String, targetKey: String) {
        try {
            dependencies.s3ClientServicePart.moveS3Directory(bucketName, sourceKey, targetKey)
        } catch (e: AmazonS3Exception) {
            StructuredLogger.error(
                "S3_MOVE_FAILED", e, mapOf(
                    "bucket" to bucketName,
                    "sourceKey" to sourceKey,
                    "targetKey" to targetKey,
                    "statusCode" to e.statusCode
                )
            )
            throw PersistenceException("S3 이동 실패: " + e.message)
        } catch (e: SdkClientException) {
            StructuredLogger.error(
                "S3_SDK_CLIENT_ERROR", e, mapOf(
                    "bucket" to bucketName,
                    "sourceKey" to sourceKey,
                    "targetKey" to targetKey
                )
            )
            throw PersistenceException("S3 클라이언트 오류: " + e.message)
        }
    }

    /**
     * 객체의 첨부파일들을 가져온다.
     *
     * @param persistable
     * @return
     */
    fun getAttachmentsByReference(persistable: Persistable): MutableList<Attachment?> {
        return dependencies.attachmentQueryRepository.findAttachmentsByReference(persistable)
    }

    /**
     * 파일을 삭제한다.
     * 파일 변경시 s3에 존재하는 초기 압축 해제 파일을 모두 삭제 해야한다.
     *
     * @param bucketName
     * @param sourceKey
     * @throws PersistenceException
     */
    @Throws(PersistenceException::class)
    fun deleteS3Files(bucketName: String?, sourceKey: String?) {
        try {
            dependencies.s3ClientServicePart.deleteS3Files(bucketName, sourceKey)
        } catch (e: SdkClientException) {
            throw PersistenceException(e)
        }
    }

    /**
     * 연결된 첨부파일 객체를 삭제한다.
     *
     * @param reference
     */
    @Transactional(rollbackFor = [Exception::class])
    fun deleteAttachmentsByReference(reference: Persistable) {
        dependencies.attachmentQueryRepository.deleteAttachmentByReference(reference)
    }

    /**
     * 연결된 첨부파일 객체를 삭제한다.
     *
     * @param reference
     */
    @Transactional(rollbackFor = [Exception::class])
    fun deleteAttachmentsByReferenceAndType(reference: Persistable, fileType: UploaderType) {
        dependencies.attachmentQueryRepository.deleteAttachmentByReference(reference, fileType)
    }


    /** /////////////////////////////////////// private
     * /////////////////////////////////////// /////////////////////////////////////////////////// */


    companion object {
        private val log: Logger = LoggerFactory.getLogger(AttachmentService::class.java)
    }
}
