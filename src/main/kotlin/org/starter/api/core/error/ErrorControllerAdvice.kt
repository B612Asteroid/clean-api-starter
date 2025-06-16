package org.starter.api.core.error


import jakarta.servlet.http.HttpServletRequest
import org.apache.catalina.connector.ClientAbortException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.starter.api.core.log.StructuredLogger
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
        StructuredLogger.error("USER_NOT_FOUND", e, request.parameterMap)
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
     * 컨트롤러에서 기타 에러가 발생하였을 때
     *
     * @param e
     * @param request
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PersistenceException::class)
    fun handle(e: PersistenceException, request: HttpServletRequest): ResponseEntity<ResponseObject> {
        // #. 클래스 내부 에러일 때,
        StructuredLogger.error("PERSISTENCE_ERROR", e, request.parameterMap)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body(
                ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value().toString(),
                    e,
                    false,
                    e.message,
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
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handle(e: Exception, request: HttpServletRequest): ResponseEntity<ResponseObject> {
        // #. 그 외 내부 에러일 때
        StructuredLogger.error("INTERNAL_ERROR", e, request.parameterMap)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body(
                ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value().toString(),
                    e,
                    false,
                    "알수 없는 오류가 발생했습니다.",
                    null
                )
            )
    }


    companion object {
        private val log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(ErrorControllerAdvice::class.java)
    }
}
