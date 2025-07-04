package org.starter.api.app.enums

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.starter.api.core.BaseController
import org.starter.api.core.response.ResponseObject

/**
 * @author gsong
 * @email gsong@chunjae.co.kr
 * @create date 2025-06-26 09:25:177
 * @modify date 2025-06-26 09:25:177
 * @desc 정의한 Enums를 내려주는 컨트롤러
 */
@RestController
@RequestMapping("/enums")
@Tag(name = "상품 컨트롤러")
class EnumsController(private val enumService: EnumService) : BaseController() {

    @GetMapping("/user")
    @Operation(summary = "유저 코드 리턴", description = "유저 관련 공통 코드를 가져온다.")
    fun getUserEnums(): ResponseEntity<ResponseObject> {
        return this.createSuccessResponseJson(enumService.getUserEnums())
    }
}