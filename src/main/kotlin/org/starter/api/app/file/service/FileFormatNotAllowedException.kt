package org.starter.api.app.file.service

import org.starter.api.core.error.PersistenceException

/**
 * 파일 포멧이 지원하는 타입과 맞지 않을 경우 사용하는 Exception
 */
class FileFormatNotAllowedException : PersistenceException {
    constructor(message: String?) : super(message)

    constructor(e: Exception?) : super(e)
}