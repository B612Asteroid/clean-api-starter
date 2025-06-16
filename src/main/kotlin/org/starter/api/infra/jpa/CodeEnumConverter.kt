package org.starter.api.infra.jpa

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * 타입핸들러 조상 클래스
 * Enum 사용 시 타입핸들러를 오버라이드 받은 메소드 사용
 * @param <E>
</E> */
@Converter
open class CodeEnumConverter<E>(private val type: Class<E>) : AttributeConverter<CodeEnum, String> where E : Enum<E>, E : CodeEnum {
    public override fun convertToEntityAttribute(dbData: String): CodeEnum {
        return this.getCodeEnum(dbData)
    }

    public override fun convertToDatabaseColumn(codeEnum: CodeEnum?): String? {
        return if (codeEnum != null) codeEnum.code else null
    }

    private fun getCodeEnum(code: String): CodeEnum {
        return this.getCodeEnum(type, code)
    }

    /**
     * code 값을 Enum으로 변환해준다.
     *
     * @param type
     * @param code
     * @param <T>
     * @return
    </T> */
    @Throws(TypeCastException::class, NullPointerException::class)
    fun getCodeEnum(type: Class<out CodeEnum>, code: String): E {
        val enumConstants: Array<CodeEnum> = type.enumConstants as Array<CodeEnum>
        for (codeNum in enumConstants) {
            if (codeNum.code.equals(code)) {
                return codeNum as E
            }
        }
        throw NullPointerException("없음.")
    }
}
