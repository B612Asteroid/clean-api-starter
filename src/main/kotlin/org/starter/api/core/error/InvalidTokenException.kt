package org.starter.api.core.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidTokenException(message: String?) : ForbiddenException(message)
