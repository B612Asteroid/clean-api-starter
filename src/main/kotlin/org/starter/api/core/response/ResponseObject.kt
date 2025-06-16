package org.starter.api.core.response

import java.io.Serializable

/**
 * Response object
 *
 * @constructor Create empty Response object
 */
open class ResponseObject : Serializable {
    // #. 상태 (400, 404 등)
    var status: String? = null

    // #. 기타 필요한 데이터
    var data: Any? = null

    // #. 성공 여부
    var isSuccess: Boolean = false

    // #. 실제 메시지
    var message: String? = null

    constructor(status: String?, data: Any?, success: Boolean, message: String?) {
        this.status = status
        this.data = data
        this.isSuccess = success
        this.message = message
    }

    constructor()
}


