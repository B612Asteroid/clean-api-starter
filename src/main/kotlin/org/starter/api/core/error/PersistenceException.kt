package org.starter.api.core.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * 최상위 Exception
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class PersistenceException : Exception {
    // 생성자
    constructor(message: String?) : super(message)

    // 생성자
    constructor(e: Exception?) : super(e)
}
