package org.starter.api.infra.persistence

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.sql.Timestamp

/**
 * 모든 클래스들의 조상
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@MappedSuperclass
@DynamicInsert
@DynamicUpdate
@EntityListeners(
    AuditingEntityListener::class
)
abstract class Persistable {

    /* 식별자 */
    abstract var id: Long?
        protected set

    /* 식별을 위한 className */
    @Transient
    open val className: String = this.javaClass.name

    @Column(updatable = false, insertable = false)
    @CreatedDate
    var createDate: Timestamp? = null

    @Column(insertable = false)
    @LastModifiedDate
    var updateDate: Timestamp? = null

    @get:Transient
    val oid: String
        get() = this.className + ":" + this.id

    // 생성자, 수정자 추가
    @CreatedBy
    @Column(name = "creator_id", updatable = false)
    @JsonIgnore
    var creator: String = ""

    @LastModifiedBy
    @Column(name = "updater_id")
    @JsonIgnore
    var updater: String = ""

    /**
     * 빈 생성자
     */
    constructor() {
    }

    /**
     * 아이디가 존재하는 생성자
     *
     * @param id
     */
    protected constructor(id: Long?) {
        this.id = id
    }

    val isNew: Boolean
        get() = this.id?.let { it <= 0L } ?: true

    fun markAsNew() {
        this.id = null
    }

    /**
     * Override from Object
     *
     * @return
     */
    override fun toString(): String {
        return className + ":" + this.id
    }

    /**
     * Override from Object
     *
     * @return
     */
    override fun equals(other: Any?): Boolean {
        if (other is Persistable && !other.isNew) {
            return other.oid == this.oid
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

}
