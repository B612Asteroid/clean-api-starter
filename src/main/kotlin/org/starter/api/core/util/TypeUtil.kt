package org.starter.api.core.util

import org.starter.api.infra.jpa.CodeEnum
import java.util.*

/**
 * 타입 체크용 유틸 클래스
 */
object TypeUtil {
    /**
     * Long Value체크
     * 객체가 Long이 아니면 0 리턴
     *
     * @param obj
     * @return
     */
    fun longValue(obj: Any?): Long {
        var value = 0L
        try {
            value = obj.toString().toLong()
            return value
        } catch (_: NumberFormatException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        } catch (_: NullPointerException) {
            return value
        }
    }

    /**
     * Long Value체크
     * 객체가 Long이 아니면 0 리턴
     *
     * @param obj
     * @return
     */
    fun longValue(obj: Any?, defaultValue: Long): Long {
        var value = defaultValue
        try {
            value = obj.toString().toLong()
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        } catch (_: NumberFormatException) {
            return value
        }
    }

    /**
     * Long Value체크
     * 객체가 Long이 아니면 null 리턴
     *
     * @param obj
     * @return
     */
    fun longObject(obj: Any?): Long? {
        var value: Long? = null
        try {
            value = obj.toString().toLong()
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        } catch (_: NumberFormatException) {
            return value
        }
    }

    /**
     * Boolean Value체크
     * 객체가 Boolean이 아니면 false
     *
     * @param obj
     * @return
     */
    fun booleanValue(obj: Any?): Boolean {
        var value = false
        try {
            value = obj.toString().toBoolean()
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        }
    }

    /**
     * Boolean Value체크
     * 객체가 Boolean이 아니면 false
     *
     * @param obj
     * @return
     */
    fun booleanValue(obj: Any?, defaultValue: Boolean): Boolean {
        var value = defaultValue
        try {
            value = obj.toString().toBoolean()
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        }
    }

    /**
     * Boolean Value체크
     * 객체가 Boolean이 아니면 null 리턴
     *
     * @param obj
     * @return
     */
    fun booleanObject(obj: Any?): Boolean? {
        var value: Boolean? = null
        try {
            value = obj.toString().toBoolean()
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        }
    }

    /**
     * int Value체크
     * 객체가 int가 아니면 0
     *
     * @param obj
     * @return
     */
    fun intValue(obj: Any?): Int {
        var value = 0
        try {
            value = obj.toString().toInt()
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        } catch (_: NumberFormatException) {
            return value
        }
    }

    /**
     * int Value체크
     * 객체가 int가 아니면 0
     *
     * @param obj
     * @return
     */
    fun intValue(obj: Any?, defaultValue: Int): Int {
        var value = defaultValue
        try {
            value = obj.toString().toInt()
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        } catch (_: NumberFormatException) {
            return value
        }
    }

    /**
     * int Object 체크
     * 객체가 int가 아니면 null 리턴
     *
     * @param obj
     * @return
     */
    fun integerObject(obj: Any?): Int? {
        var value: Int? = null
        try {
            if (!Objects.isNull(obj)) {
                value = obj.toString().toInt()
            }
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        } catch (_: NumberFormatException) {
            return value
        }
    }

    /**
     * float Value체크
     * 객체가 float가 아니면 0
     *
     * @param obj
     * @return
     */
    fun floatValue(obj: Any?): Float {
        var value = 0f
        try {
            value = obj.toString().toFloat()
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        } catch (_: NumberFormatException) {
            return value
        }
    }

    /**
     * float Value체크
     * 객체가 float가 아니면 0
     *
     * @param obj
     * @return
     */
    fun floatValue(obj: Any?, defaultValue: Float): Float {
        var value = defaultValue
        try {
            value = obj.toString().toFloat()
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        } catch (_: NumberFormatException) {
            return value
        }
    }

    /**
     * Float Object 체크
     * 객체가 int가 아니면 null 리턴
     *
     * @param obj
     * @return
     */
    fun floatObject(obj: Any?): Float? {
        var value: Float? = null
        try {
            value = obj.toString().toFloat()
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        } catch (_: NumberFormatException) {
            return value
        }
    }

    /**
     * double Value체크
     * 객체가 double이 아니면 0
     *
     * @param obj
     * @return
     */
    fun doubleValue(obj: Any?): Double {
        var value = 0.0
        try {
            value = obj.toString().toDouble()
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        } catch (_: NumberFormatException) {
            return value
        }
    }

    /**
     * double Value체크
     * 객체가 double이 아니면 0
     *
     * @param obj
     * @return
     */
    fun doubleValue(obj: Any?, defaultValue: Double): Double {
        var value = defaultValue
        try {
            value = obj.toString().toDouble()
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        } catch (_: NumberFormatException) {
            return value
        }
    }

    /**
     * Double Object 체크
     * 객체가 int가 아니면 null 리턴
     *
     * @param obj
     * @return
     */
    fun doubleObject(obj: Any?): Double? {
        var value: Double? = null
        try {
            value = obj.toString().toDouble()
            return value
        } catch (_: NullPointerException) {
            // 아무것도 안해야 실패 시 정상적으로 0이 리턴
            return value
        } catch (_: NumberFormatException) {
            return value
        }
    }

    /**
     * Object가 Null인지 체크함.
     * Null 이면 ""를 리턴, Null이 아니면 Object를 String으로 변환하여 리턴
     *
     * @param object
     * @return
     */
    fun stringValue(`object`: Any?): String {
        var result: String? = ""
        if (`object` == null || `object`.toString() == "null" || `object`.toString().isEmpty()) {
            result = ""
        } else {
            result = `object`.toString()
        }
        return result
    }

    fun stringValue(`object`: Any?, res: String?): String {
        var result: String? = ""
        if (`object` == null || `object`.toString() == "null" || `object`.toString().isEmpty()) {
            result = ""
            if (res != null) {
                result = res
            }
        } else {
            result = `object`.toString()
        }
        return result
    }

    /**
     * 해당 오브젝트에 해당하는 값을 리턴한다.
     *
     * @param object
     * @param clazz
     * @param <T>
    </T> */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(`object`: Any?, clazz: Class<T?>): T? {
        var t: T? = null
        try {
            if (clazz.isInstance(`object`)) {
                t = `object` as T
            }
            return t
        } catch (_: NullPointerException) {
            return null
        } catch (_: ClassCastException) {
            return null
        }
    }

    /**
     * code 값을 Enum으로 변환해준다.
     *
     * @param type
     * @param code
     * @param <T>
     * @return
    </T> */
    @Suppress("UNCHECKED_CAST")
    @Throws(TypeCastException::class, NullPointerException::class)
    fun <E> getCodeEnum(type: Class<out CodeEnum>, code: String): E {
        val enumConstants: Array<CodeEnum> = type.enumConstants as Array<CodeEnum>
        for (codeNum in enumConstants) {
            if (codeNum.code == code) {
                return codeNum as E
            }
        }
        throw IllegalArgumentException("없음.")
    }

    /**
     * Object에 대한 Null 체킹
     *
     * @param object
     * @return
     */
    fun isNull(`object`: Any?): Boolean {
        return Objects.isNull(`object`) || stringValue(`object`).isEmpty()
    }

    /**
     * Object Not NULL 체킹
     *
     * @param object
     * @return
     */
    fun notNull(`object`: Any?): Boolean {
        return !isNull(`object`)
    }
}
