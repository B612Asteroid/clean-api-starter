package org.starter.api.infra.jpa

import java.io.Serializable

/**
 * 모든 Enum들의 부모 인터페이스
 */
interface CodeEnum : Serializable {
    /**
     * getter 역할을 함
     * @return
     */
    val code: String

    /**
     * 라벨로 쓸 이름
     */
    val label: String
}