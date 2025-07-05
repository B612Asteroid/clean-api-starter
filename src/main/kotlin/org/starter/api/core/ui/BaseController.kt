package org.starter.api.core.ui

import org.starter.api.core.util.TypeUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.starter.api.core.ui.response.ErrorResponse
import org.starter.api.core.ui.response.ResponseObject
import org.starter.api.core.ui.response.SuccessResponse

/**
 * 모든 컨트롤러의 베이스가 되는 클래스
 * 모든 MVC 컨트롤러는 이 클래스를 상속받아서 사용해야 합니다.
 */
open class BaseController : MessageSourceAware {
    private var messageSource: MessageSource? = null

    override fun setMessageSource(messageSource: MessageSource) {
        this.messageSource = messageSource
    }


    // ############################## 서비스 선언
    // ############################## Protected 하위 Controller 공통 메쏘드


    /**
     * 메소드 오버로딩
     * 성공 객체를 만들어 보낸다.
     *
     * @param obj
     * @return
     */
    protected fun createSuccessResponseJson(obj: Any?): ResponseEntity<ResponseObject> {
        return this.createSuccessResponseJson(obj, "")
    }

    /**
     * 화면으로 성공 JSON을 만들어 보내준다.
     *
     * @param responseObject
     * @param message
     * @return
     */
    protected fun createSuccessResponseJson(responseObject: Any?, message: String?): ResponseEntity<ResponseObject> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                SuccessResponse(
                    "200",
                    responseObject,
                    true,
                    message
                )
            )
    }

    /**
     * 오버로딩, 에러 메시지를 넘긴다.
     *
     * @param e
     * @return
     * @throws Exception
     */
    protected fun createErrorResponseJson(e: Exception): ResponseEntity<ResponseObject> {
        return this.createErrorResponseJson(e, e.message, null)
    }

    /**
     * 오버로딩, 에러 메시지를 넘긴다.
     *
     * @param e
     * @return
     */
    protected fun createErrorResponseJson(e: Exception, addition: Any?): ResponseEntity<ResponseObject> {
        return this.createErrorResponseJson(e, e.message, addition)
    }

    /**
     * 에러 메시지를 맵핑해서 넘긴다.
     *
     * @param e
     * @param message
     * @return
     * @throws Exception
     */
    protected fun createErrorResponseJson(
        e: Exception,
        message: String?,
        addition: Any?
    ): ResponseEntity<ResponseObject> {
        return this.createErrorResponseJson(HttpStatus.INTERNAL_SERVER_ERROR, e, message, addition)
    }

    /**
     * 에러 메시지를 맵핑해서 넘긴다.
     *
     * @param e
     * @param message
     * @return
     * @throws Exception
     */
    protected fun createErrorResponseJson(
        status: HttpStatus, e: Exception, message: String?,
        addition: Any?
    ): ResponseEntity<ResponseObject> {
        return ResponseEntity
            .status(status)
            .body(
                ErrorResponse(
                    TypeUtil.stringValue(status.value()),
                    e,
                    false,
                    message,
                    addition
                )
            )
    }


    // ############################## Private
    /**
     * cause 에서 재귀적으로 특정 Exception 클래스를 찾는다
     *
     * @param e
     * @param cls
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T> findExceptionRecursively(e: Throwable, cls: Class<out Throwable>): T? {
        if (e.javaClass == cls) {
            return e as T
        } else {
            val cause = e.cause
            return if (cause != null) {
                findExceptionRecursively<T>(cause, cls)
            } else {
                null
            }
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BaseController::class.java)
    }
}
