package org.starter.api.app.file.service

import org.starter.api.app.file.domain.Attachment
import org.starter.api.core.AppProperties
import org.starter.api.core.MessageSourceWrapper
import org.starter.api.core.error.PersistenceException
import org.starter.api.core.util.TypeUtil.isNull
import org.starter.api.core.util.TypeUtil.stringValue
import org.starter.api.infra.persistence.PersistenceService
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author gsong
 * @create date 2025-06-23 12:00:174
 * @modify date 2025-06-23 12:00:174
 * @desc 한번에 S3, DB업로드를 진행하게 해주는 서비스 오케스트레이터
 */
@Component
class AttachmentUploadUseCase(
    private val attachmentService: AttachmentService,
    private val appProperties: AppProperties,
    private val messageSourceWrapper: MessageSourceWrapper
) {

    // #. 포함되어서는 안되는 확장자
    protected val DISALLOWED_FILE_SUFFIX: Array<String?> = arrayOf("jsp", "jspx")

    // #. 허용 확장자
    protected val ALLOWED_FILE_SUFFIX: Array<String?> = arrayOf(
        "pdf", "mp3", "flac", "wav", "jpg", "png", "bmp", "jpeg",
        "mp4", "avi", "wmv", "mov", "mkv", "pptx", "xlsx", "xls", "html", "css", "js", "zip", "blob", "hwp", "doc",
        "docx", "smi", "vtt", "txt"
    )

    /**
     * 컨텐츠를 업로드 하고 객체를 반환한다.
     *
     * @return
     * @throws PersistenceException
     */
    @Throws(PersistenceException::class)
    fun execute(attachmentUploadCommand: AttachmentUploadCommand): Attachment {
        val attachment = Attachment()

        if (attachmentUploadCommand.file.isEmpty()) {
            // #. MultipartFile 존재하지 않음
            throw PersistenceException(messageSourceWrapper.get("UPLOAD.ERR012"))
        }

        var base: String = appProperties.path.root

        if (!isNull(attachmentUploadCommand.rootPath)) {
            base += "/" + attachmentUploadCommand.rootPath
        }

        var originFileName =
            stringValue(attachmentUploadCommand.file.originalFilename, "").lowercase(Locale.getDefault())
        // #. 파일 포멧 판별
        val finalOriginFileName = originFileName

        if (Arrays.stream<String?>(DISALLOWED_FILE_SUFFIX)
                .anyMatch { suffix: String -> finalOriginFileName.endsWith(suffix) }
        ) {
            // #. 해당 포멧은 업로드 할 수 없습니다.
            throw FileFormatNotAllowedException(messageSourceWrapper.get("UPLOAD.ERR03"))
        }
        if (Arrays.stream<String?>(ALLOWED_FILE_SUFFIX)
                .noneMatch { suffix: String -> originFileName.endsWith(suffix) }
        ) {
            // #. 해당 포멧은 업로드 할 수 없습니다.
            throw FileFormatNotAllowedException(messageSourceWrapper.get("UPLOAD.ERR03"))
        }

        if ("blob" == originFileName) {
            val dataFormat = SimpleDateFormat("yyyyMMddHHmmSS")
            val cal = Calendar.getInstance()
            val time = dataFormat.format(cal.getTime())
            originFileName = time + "_" + UUID.randomUUID() + ".png"
        }
        val originPath = Path.of(base, originFileName).toString().replace("\\", "/")
        attachmentService.s3Upload(attachmentUploadCommand.file, originPath)

        attachment.name = originFileName
        attachment.fileType = attachmentUploadCommand.fileType
        attachment.originPath = originPath
        attachment.filePath = originPath
        attachment.referenceId = attachmentUploadCommand.reference.id
        attachment.referenceClass = attachmentUploadCommand.reference.className
        attachment.deleteYn = false
        attachment.orders = attachmentUploadCommand.order
        return attachmentService.saveDBUpload(attachment)
    }

}