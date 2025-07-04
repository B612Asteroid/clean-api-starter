package org.starter.api.infra.jpa

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.starter.api.core.util.StringUtil

@Converter
class BooleanToYNConverter : AttributeConverter<Boolean, String> {

    override fun convertToDatabaseColumn(attribute: Boolean?): String =
        StringUtil.getBooleanStr(attribute)

    override fun convertToEntityAttribute(dbData: String?): Boolean =
        StringUtil.isYn(dbData)
}