package org.starter.api.core.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
open class ForbiddenException : RuntimeException {
    constructor(message: String?) : super(message)

    constructor(e: ForbiddenException?) : super(e)
}
