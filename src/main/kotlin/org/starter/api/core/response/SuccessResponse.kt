package org.starter.api.core.response

/**
 * Success response
 *
 * @constructor Create empty Success response
 */
class SuccessResponse : ResponseObject {
    constructor(status: String?, data: Any?, success: Boolean, message: String?) : super(status, data, success, message)

    constructor()
}
