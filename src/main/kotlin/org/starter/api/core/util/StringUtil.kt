package org.starter.api.core.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

object StringUtil {

    fun getBooleanStr(flag: Boolean? = false): String {
        return if (flag == true) "Y" else "N"
    }

    fun isYn(yn: String?): Boolean {
        return "Y" == yn
    }
}
