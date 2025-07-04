package org.starter.api.app.file.service

import org.starter.api.app.file.domain.UploaderType
import org.starter.api.infra.persistence.Persistable
import org.springframework.web.multipart.MultipartFile

data class AttachmentUploadCommand(
    /* 업로드하고 만들어줄 콘텐츠 객체 */
    val reference: Persistable,

    /* 실제로 업로드할 파일 */
    val file: MultipartFile,

    /* 업로드 파일 타입 */
    val fileType: UploaderType = UploaderType.DEFAULT,

    /* 업로드 수정 여부를 위한 아이디 */
    val id: Long? = null,

    /* Attachment 다음으로 파일 앞에 들어가는 패스 */
    val rootPath: String? = null,

    val order: Int = 0,
)