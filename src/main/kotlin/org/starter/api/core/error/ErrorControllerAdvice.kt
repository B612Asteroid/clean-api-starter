package org.starter.api.core.error


import jakarta.servlet.http.HttpServletRequest
import org.apache.catalina.connector.ClientAbortException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.starter.api.core.response.ErrorResponse
import org.starter.api.core.response.ResponseObject

@ControllerAdvice
class ErrorControllerAdvice {
    /**
     * Admin이 아니면 Admin 로그인 화면으로 이동
     *
     * @param e
     * @param request
     * @return
     */
    @ResponseStatus(org.springframework.http.HttpStatus.FORBIDDEN)
    @org.springframework.web.bind.annotation.ExceptionHandler(NotAdminException::class)
    fun handle(e: NotAdminException, request: HttpServletRequest): ResponseEntity<ResponseObject?> {
        return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN.value()).body<ResponseObject?>(
            ErrorResponse(
                "403",
                e,
                false,
                e.localizedMessage,
                null
            )
        )
    }


    /**
     * 잘못된 토큰 에러
     *
     * @param e
     * @param request
     * @return
     */
    @ResponseStatus(org.springframework.http.HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException::class)
    fun handle(e: InvalidTokenException, request: HttpServletRequest?): ResponseEntity<ResponseObject?> {
        return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED.value()).body<ResponseObject?>(
            ErrorResponse(
                "401",
                e,
                false,
                e.localizedMessage,
                null
            )
        )
    }

    /**
     * 잘못된 토큰 에러
     *
     * @param e
     * @param request
     * @return
     */
    @ResponseStatus(org.springframework.http.HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException::class)
    fun handle(e: ForbiddenException, request: HttpServletRequest?): ResponseEntity<ResponseObject?> {
        return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN.value()).body<ResponseObject?>(
            ErrorResponse(
                "403",
                e,
                false,
                e.localizedMessage,
                null
            )
        )
    }



    /**
     * 404
     *
     * @param e
     * @param request
     * @return
     */
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    fun handle(e: NotFoundException, request: HttpServletRequest?): ResponseEntity<ResponseObject?> {
        return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND.value()).body<ResponseObject?>(
            ErrorResponse(
                "404",
                e,
                false,
                e.getLocalizedMessage(),
                null
            )
        )
    }

    /**
     * 컨트롤러에서 기타 에러가 발생하였을 때
     *
     * @param e
     * @param request
     * @return
     */
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ClientAbortException::class)
    fun handle(e: ClientAbortException, request: HttpServletRequest) {
        // #. 무시
        log.error(
            "{} ==> {} | {}",
            e.message,
            request.remoteAddr,
            request.requestURI
        )
    }

    /**
     * Spring Security UsernameNotFoundException
     *
     * @param e
     * @param request
     * @return
     */
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UsernameNotFoundException::class)
    fun handle(e: UsernameNotFoundException, request: HttpServletRequest): ResponseEntity<ResponseObject?> {
        StructuredLogger.INSTANCE.error("USER_NOT_FOUND", e, request.getParameterMap())
        return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED.value()).body<ResponseObject?>(
            ErrorResponse(
                "401",
                e,
                false,
                e.getLocalizedMessage(),
                null
            )
        )
    }


    /**
     * 컨트롤러에서 기타 에러가 발생하였을 때
     *
     * @param e
     * @param request
     * @return
     */
    @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    @org.springframework.web.bind.annotation.ExceptionHandler(PersistenceException::class)
    fun handle(e: PersistenceException, request: HttpServletRequest?): ResponseEntity<ResponseObject?> {
        // #. 클래스 내부 에러일 때,
        StructuredLogger.INSTANCE.error("PERSISTENCE_ERROR", e, java.util.HashMap<K?, V?>())
        return ResponseEntity
            .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body<ResponseObject?>(
                ErrorResponse(
                    TypeUtil.stringValue(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    e,
                    false,
                    e.getMessage(),
                    null
                )
            )
    }


    /**
     * 컨트롤러에서 기타 에러가 발생하였을 때
     *
     * @param e
     * @param request
     * @return
     */
    @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    @org.springframework.web.bind.annotation.ExceptionHandler(java.lang.Exception::class)
    fun handle(e: java.lang.Exception?, request: HttpServletRequest?): ResponseEntity<ResponseObject?> {
        // #. 그 외 내부 에러일 때
        StructuredLogger.INSTANCE.error("INTERNAL_ERROR", e, java.util.HashMap<K?, V?>())
        return ResponseEntity
            .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body<ResponseObject?>(
                ErrorResponse(
                    TypeUtil.stringValue(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    e,
                    false,  // #. 서버 에러가 발생했습니다. 관리자에게 문의하세요.
                    MessageHelper.getMessage("COMMON.ERR01"),
                    null
                )
            )
    }


    companion object {
        private val log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(ErrorControllerAdvice::class.java)
    }
}
