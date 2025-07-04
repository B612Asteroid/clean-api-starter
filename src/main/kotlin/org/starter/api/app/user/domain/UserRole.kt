package org.starter.api.app.user.domain

import org.starter.api.infra.jpa.CodeEnum
import org.starter.api.infra.jpa.CodeEnumConverter


enum class UserRole (override val code: String, override val label: String): CodeEnum {
    USER("US", "사용자"),
    ADMIN("AD", "관리자");

    /**
     * 내부 타입핸들러 클래스. 마이바티스와 연동하기 위해선 다음 코드를 반드시 구현해야함
     */
    class Converter: CodeEnumConverter<UserRole>(UserRole::class.java)
}