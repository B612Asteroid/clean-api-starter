package org.starter.api.app.enums

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

/**
 * @author gsong
 * @email gsong@chunjae.co.kr
 * @create date 2025-06-26 09:22:177
 * @modify date 2025-06-26 09:22:177
 * @desc Enum을 프론트에서 사용하기 위해 내려주는 Response
 */
class EnumLabelResponse<T: Any>() : Serializable {
    @Schema(description = "화면에 표시될 라벨")
    lateinit var label: String

    @Schema(description = "실제 데이터 전송 용 코드")
    lateinit var code: T

    constructor(label: String, code: T) : this() {
        this.code = code
        this.label = label
    }
}