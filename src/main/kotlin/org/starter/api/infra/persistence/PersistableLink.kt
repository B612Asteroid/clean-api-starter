package org.starter.api.infra.persistence

import jakarta.persistence.MappedSuperclass

/**
 * 관계 표현 최상위 클래스
 * - 출발점이면 Source,
 * - 대상이면 Target
 * 에시) ContentsTopicLink에서,
 * #. Contents가 Source,
 * #. Topic이 Target.
 *
 * @author gsong
 * @email pok147@chunjae.co.kr
 * @create date 2025-05-07 07:53:127
 * @modify date 2025-05-07 07:53:127
 * @desc
 */
@MappedSuperclass
abstract class PersistableLink<Source : Persistable, Target : Persistable> protected constructor(
) : Persistable() {
    /* 출발점이 되는 클래스 */
    abstract val source: Source

    /* 도착점이 되는 클래스 */
    abstract val target: Target

    companion object {

        /**
         * Link 객체를 만들 때,
         * Link는 반드시 연결 객체가 모두 있어야만 의미를 가지므로,
         * 반드시 Of로 강제한다.
         *
         * @param source
         * @param target
         * @returnㅇ
         */
        fun <Source : Persistable, Target : Persistable> of(
            source: Source,
            target: Target
        ): PersistableLink<Source, Target> {
            throw NotImplementedError("하위 클래스에서 반드시 Of를 구현할 것")
        }
    }
}