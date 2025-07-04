package org.starter.api.infra.s3

import com.amazonaws.AmazonClientException
import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.model.*
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import jakarta.persistence.PersistenceException
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import org.starter.api.core.AppProperties
import org.starter.api.core.MessageSourceWrapper
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

/**
 * S3 관련 로직 처리
 */
@Component
class S3ClientService internal constructor(
    private val s3clientProvider: S3ClientProvider,
    private val messageSourceWrapper: MessageSourceWrapper,
    private val appProperties: AppProperties
) {
    /**
     * Amazon S3에 파일 업로드 오버로딩
     *
     * @param file
     * @param fileName
     * @return
     */
    @Throws(InterruptedException::class, IOException::class, AmazonClientException::class)
    fun s3Upload(file: MultipartFile, fileName: String, contentType: String) {
        var fileName = fileName
        val amazonS3 = s3clientProvider.connection
        val tm = TransferManagerBuilder.standard().withS3Client(amazonS3).build()
        val request: PutObjectRequest?
        try {
            val metadata = ObjectMetadata()
            metadata.contentType = contentType
            metadata.contentLength = file.size

            // #. PDF 다운로드가 가능하도록 메타데이터 수정
            if (contentType.contains("pdf")) {
                metadata.contentDisposition = "attachment;"
            }

            fileName = this.cutPrefixSlash(fileName)

            request = PutObjectRequest(
                appProperties.s3.bucket,
                fileName,
                file.inputStream,
                metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead)
            val upload = tm.upload(request)
            upload.waitForCompletion()
        } finally {
            tm.shutdownNow(false)
        }
    }


    /**
     * @param cdnPath
     * @return
     * @throws PersistenceException
     */
    fun listObjects(cdnPath: String): MutableList<String> {
        val s3Client = s3clientProvider.connection
        return s3Client.listObjects(appProperties.s3.bucket, cdnPath)
            .objectSummaries
            .stream()
            .map { obj: S3ObjectSummary -> obj.getKey() }.toList()
    }

    /**
     * S3에 파일이 있는지 체크하여 true/false로 return
     *
     * @param filePath
     * @return
     */
    @Throws(AmazonClientException::class)
    fun isExistFile(filePath: String): Boolean {
        var filePath = filePath
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1)
        }

        val bucketName: String? = appProperties.s3.bucket
        val s3Client = s3clientProvider.connection
        val `object` = s3Client.getObject(bucketName, filePath)
        return `object` != null
    }

    /**
     * S3로부터 파일을 다운로드 받는다.
     *
     * @param filePath
     * @return
     */
    @Throws(PersistenceException::class)
    fun s3Download(bucketName: String?, filePath: String): ByteArray {
        try {
            val s3Client = s3clientProvider.connection
            val `object` = s3Client.getObject(bucketName, this.cutPrefixSlash(filePath))
            val objectInputStream = `object`.getObjectContent()

            return IOUtils.toByteArray(objectInputStream)
        } catch (e: SdkClientException) {
            throw PersistenceException(e)
        } catch (e: IOException) {
            throw PersistenceException(e)
        }
    }

    /**
     * 파일을 다른 파일로 복사한다.
     *
     * @param orgKey
     * @param copyKey
     */
    @Throws(AmazonClientException::class)
    fun copyFromS3(orgKey: String, copyKey: String) {
        var orgKey = orgKey
        var copyKey = copyKey
        val amazonS3 = s3clientProvider.connection
        val bucket: String? = appProperties.s3.bucket

        orgKey = this.cutPrefixSlash(orgKey)
        copyKey = this.cutPrefixSlash(copyKey)

        // copy 객체 생성
        val copyObjectRequest = CopyObjectRequest(bucket, orgKey, bucket, copyKey)
            .withCannedAccessControlList(CannedAccessControlList.PublicRead)

        // copy
        amazonS3.copyObject(copyObjectRequest)
    }

    /**
     * S3 폴더 복사
     *
     * @param bucketName // 대상 버킷 이름
     * @param sourceKey  // 대상 키
     */
    @Throws(SdkClientException::class)
    fun copyFromS3Directory(bucketName: String?, sourceKey: String, targetKey: String) {
        val s3Client = s3clientProvider.connection

        // S3 버킷 리스트 조회
        val listObjectsV2Request = ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(sourceKey)

        val listObjectsV2Result = s3Client.listObjectsV2(listObjectsV2Request)

        for (s3ObjectSummary in listObjectsV2Result.objectSummaries) {
            val objectSourceKey = s3ObjectSummary.getKey()
            val objectDestinationKey = objectSourceKey.replaceFirst(sourceKey.toRegex(), targetKey)

            this.copyFromS3(objectSourceKey, objectDestinationKey)
        }
    }

    /**
     * 파일을 이동한다.
     * 기본적으로 S3는 이동이라는 개념이 존재하지 않기 때문에 복사 후 삭제하는 방법을 사용한다.
     *
     * @param bucketName
     * @param sourceKey
     * @param targetKey
     */
    @Throws(SdkClientException::class)
    fun moveS3Directory(bucketName: String?, sourceKey: String, targetKey: String) {
        val s3Client = s3clientProvider.connection

        // S3 버킷 리스트 조회
        val listObjectsV2Request = ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(sourceKey)
        val listObjectsV2Result = s3Client.listObjectsV2(listObjectsV2Request)

        for (s3ObjectSummary in listObjectsV2Result.getObjectSummaries()) {
            val objectSourceKey = s3ObjectSummary.getKey()
            val objectDestinationKey = objectSourceKey.replaceFirst(sourceKey.toRegex(), targetKey)

            this.copyFromS3(objectSourceKey, objectDestinationKey)
            this.deleteS3File(objectSourceKey)
        }
    }

    /**
     * 파일을 삭제한다.
     *
     * @param key
     */
    fun deleteS3File(key: String?) {
        val s3Client = s3clientProvider.connection
        // Delete 객체 생성
        val deleteObjectRequest = DeleteObjectRequest(
            appProperties.s3.bucket, key
        )

        // Delete
        s3Client.deleteObject(deleteObjectRequest)
    }

    /**
     * 파일을 삭제한다.
     * 파일 변경시 s3에 존재하는 초기 압축 해제 파일을 모두 삭제 해야한다.
     *
     * @param bucketName
     * @param sourceKey
     * @throws PersistenceException
     */
    fun deleteS3Files(bucketName: String?, sourceKey: String?) {
        val s3Client = s3clientProvider.connection
        val listObjectsV2Request = ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(sourceKey)

        val listObjectsV2Result = s3Client.listObjectsV2(listObjectsV2Request)
        val keys =
            listObjectsV2Result.objectSummaries.map { s3ObjectSummary: S3ObjectSummary ->
                DeleteObjectsRequest.KeyVersion(
                    s3ObjectSummary.getKey()
                )
            }.toList()
        if (!keys.isEmpty()) {
            val request = DeleteObjectsRequest(bucketName)
            request.setKeys(keys)
            s3Client.deleteObjects(request)
        }
    }

    /**
     * S3로부터 파일을 다운로드 받는다. File로 변환해서 리턴한다.
     *
     * @param filePath
     * @return
     * @throws PersistenceException
     * @throws IOException
     */
    @Throws(PersistenceException::class, IOException::class)
    fun s3DownloadToFile(filePath: String): File {
        var filePath = filePath
        val root = Files.createTempDirectory("temp-upload").toFile()

        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath
        }

        val tempFolder = Paths.get(root.getPath(), "temp").toFile()
        if (!tempFolder.exists()) {
            if (!tempFolder.mkdir()) {
                // #. 파일 업로드에 실패했습니다.
                throw PersistenceException(messageSourceWrapper.get("UPLOAD.ERR013"))
            }
        }
        val tempFile = File(tempFolder.getPath() + filePath)
        FileUtils.writeByteArrayToFile(
            tempFile,
            this.s3Download(appProperties.s3.bucket, filePath)
        )
        return tempFile
    }

    /** //////////////////////////////////////// private */ /*
     * #. s3에서 시작이 / 일 경우 파일 이름의 하나로 봄 ==> 앞자리 잘라줌
     *
     * @param fileName
     *
     * @return
     */
    private fun cutPrefixSlash(fileName: String): String {
        var fileName = fileName
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1)
        }

        return fileName
    }
}
