package org.starter.api.app.file.ui

import io.swagger.v3.oas.annotations.media.Schema
import org.starter.api.infra.persistence.Persistable

open class AttachmentDTO {
    @Schema(description = "업로드 아이디")
    var id: Long? = null

    @Schema(description = "파일명")
    var name: String? = null

    @Schema(description = "원본 파일 경로")
    var originPath: String? = null

    @Schema(description = "파일 경로")
    var filePath: String? = null

    @Schema(description = "파일 타입")
    var fileType: String = "ME"

    @Schema(description = "대체 텍스트")
    var altText: String? = null

    @Schema(description = "연결 기기 타입")
    var deviceType: String = "PC"

    @Schema(description = "파일 내 순서")
    var orders: Int = 1

    @Schema(description = "어디서 왔는지 체크")
    var uploadOriginType: String = "UI"

    @Schema(description = "업로드 상황")
    var uploadStatus: String = "PD"

    @Schema(description = "사용 기기")
    var referenceId: Long? = null

    @Schema(description = "사용 기기")
    var referenceClass: String? = null

    fun setReference(reference: Persistable) {
        this.referenceId = reference.id
        this.referenceClass = reference::class.java.name
    }
}