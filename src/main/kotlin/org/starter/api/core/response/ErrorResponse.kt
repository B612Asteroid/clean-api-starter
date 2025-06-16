package org.starter.api.core.response

/**
 * Error response
 *
 * @constructor
 *
 * @param status
 * @param e
 * @param success
 * @param message
 * @param data
 */
class ErrorResponse(status: String?, e: Throwable, success: Boolean, message: String?, data: Any?) :
    ResponseObject(status, data, success, message) {
    var className: String?

    init {
        this.message = message ?: e.localizedMessage
        this.className = e.javaClass.getName()
    }
}
