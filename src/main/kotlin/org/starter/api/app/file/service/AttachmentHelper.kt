package org.starter.api.app.file.service

import org.starter.api.app.file.domain.UploaderType
import org.starter.api.core.util.DateUtil
import org.starter.api.core.util.TypeUtil
import org.apache.commons.io.FilenameUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

/**
 * 파일 uploader Helper 클래스
 */
object AttachmentHelper {
    private val log: Logger = LoggerFactory.getLogger(AttachmentHelper::class.java)

    /**
     * 파일로부터 업로더 타입을 가져온다.
     *
     * @param mediaFiles
     * @return
     */
    fun getUploaderTypeByFile(mediaFiles: MultipartFile): UploaderType {
        if (TypeUtil.isNull(mediaFiles) || TypeUtil.isNull(mediaFiles.originalFilename)) {
            return UploaderType.DEFAULT
        }
        val fileExtension =
            TypeUtil.stringValue(FilenameUtils.getExtension(mediaFiles.originalFilename), "")
        if (fileExtension.isEmpty()) {
            return UploaderType.DEFAULT
        }

        val contentType = mediaFiles.contentType
        if (contentType == null) {
            return UploaderType.DEFAULT
        }

        if (contentType.contains("audio")) {
            return UploaderType.AUDIO
        } else if (contentType.contains("image")) {
            return UploaderType.IMAGE
        } else if (contentType.contains("video")) {
            return UploaderType.VIDEO
        } else if (contentType.contains("html")) {
            return UploaderType.HTML
        } else if (contentType.contains("pdf")) {
            return UploaderType.PDF
        } else if (contentType.contains("zip")) {
            return UploaderType.ZIP
        } else if (contentType.contains("vtt") || contentType.contains("smi")
        ) {
            return UploaderType.VTT
        } else if (fileExtension.contains("txt")) {
            return UploaderType.SCRIPT
        } else {
            return UploaderType.DEFAULT
        }
    }

    /**
     * 파일 확장자를 파싱해서 업로더 타입을 가져온다.
     *
     * @param file
     * @return
     */
    fun getUploaderTypeByFile(file: File?): UploaderType {
        if (file == null) {
            return UploaderType.DEFAULT
        }
        val fileExtension =
            TypeUtil.stringValue(FilenameUtils.getExtension(file.getName()), "")
        if (fileExtension.isEmpty()) {
            return UploaderType.DEFAULT
        }
        val FILE_EX_AUDIO = arrayOf("mp3", "wav", "flac", "aac")
        val FILE_EX_VIDEO = arrayOf("mp4", "avi", "wmv", "mov", "mkv")
        val FILE_EX_IMAGE = arrayOf("jpg", "png", "bmp", "jpeg")

        if (Arrays.stream(FILE_EX_AUDIO).anyMatch { name: String -> name.contains(fileExtension) }) {
            return UploaderType.AUDIO
        } else if (Arrays.stream(FILE_EX_IMAGE).anyMatch { name: String -> name.contains(fileExtension) }) {
            return UploaderType.IMAGE
        } else if (Arrays.stream(FILE_EX_VIDEO).anyMatch { name: String -> name.contains(fileExtension) }) {
            return UploaderType.VIDEO
        } else if (fileExtension.contains("html")) {
            return UploaderType.HTML
        } else if (fileExtension.contains("pdf")) {
            return UploaderType.PDF
        } else if (fileExtension.contains("zip")) {
            return UploaderType.ZIP
        } else {
            return UploaderType.DEFAULT
        }
    }

    /**
     * 파일 타입 스트링에서 Uploader Type을 가져옴.
     *
     * @param fileType
     * @return
     */
    fun getUploadFilePath(): String {
        return getUploadTypePath()
    }

    /**
     * 저장폴더 위치를 찾는다.
     *
     * @param type
     * @return
     */
    fun getUploadTypePath(): String {
        val sb = StringBuilder()
        sb.append(DateUtil.getDateToDateString(DateUtil.currentTimeStamp, "yyyy-MM-dd"));
//        sb.append(getPathProperty(type))
        return sb.toString()
    }


    /**
     * Contents Type 에서 Zip파일 여부를 구해온다.
     *
     * @param file
     * @return
     */
    fun isCompressed(file: MultipartFile): Boolean {
        val zipTypes = arrayOf("zip", "compressed")
        log.debug("file contentType ==> {}", file.contentType)
        val contentType = file.contentType ?: return false

        return Arrays.stream(zipTypes).anyMatch { type: String ->
            (contentType).contains(type)
        } && "zip" == StringUtils.getFilenameExtension(file.originalFilename)
    }
}
