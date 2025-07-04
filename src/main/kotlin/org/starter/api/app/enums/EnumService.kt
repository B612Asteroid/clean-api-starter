package org.starter.api.app.enums

import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.starter.api.app.user.domain.UserRole
import org.starter.api.infra.jpa.CodeEnum

/**
 * @author gsong
 * @email gsong@chunjae.co.kr
 * @create date 2025-06-26 14:03:177
 * @modify date 2025-06-26 14:03:177
 * @desc Enum 리턴 서비스. 캐싱이 되도록 진행
 */
@Service("enumService")
@CacheConfig(cacheNames = ["enums"])
class EnumService {

    @Cacheable(key = "'user'")
    fun getUserEnums(): Map<String, List<EnumLabelResponse<*>>> {
        return mutableMapOf(
            "ROLE" to UserRole.entries.map { it.toResponse() },
        )
    }

    /////////////////////// private

    /**
     * private 확장함수
     * 일반 용도로 사용하면 안되고, 반드시 해당 서비스 내에서만 사용해야 함.
     *
     * @return
     */
    private fun CodeEnum.toResponse(): EnumLabelResponse<String> {
        return EnumLabelResponse(label = this.label, code = this.code)
    }
}