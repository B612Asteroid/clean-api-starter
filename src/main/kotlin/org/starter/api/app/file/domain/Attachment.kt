package org.starter.api.app.file.domain

import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.starter.api.app.file.ui.AttachmentDTO
import org.starter.api.infra.jpa.BooleanToYNConverter
import org.starter.api.infra.persistence.Persistable

@Entity
@Table
@DynamicInsert
@DynamicUpdate
class Attachment : Persistable() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null

    /* 순서 */
    @Column
    var orders: Int = 0

    /* 이름 */
    @Column
    var name: String? = null

    /* 원본 파일 위치 */
    @Column
    var originPath: String? = null

    /* 파일 경로 */
    @Column
    var filePath: String? = null

    /* 파일 타입 */
    @Convert(converter = UploaderType.Converter::class)
    @Column
    var fileType: UploaderType = UploaderType.DEFAULT

    /* 대체 텍스트 */
    @Column
    var altText: String? = null

    /* 연결되는 객체 아이디 */
    @Column
    var referenceId: Long? = null

    /* 연결되는 객체 클래스 */
    @Column
    var referenceClass: String? = null

    /* 삭제여부 */
    @Column
    @Convert(converter = BooleanToYNConverter::class)
    var deleteYn: Boolean = false

    /**
     * 클래스를 토대로 아이디와 클래스명을 세팅한다.
     *
     * @param reference
     */
    fun setReference(reference: Persistable) {
        this.referenceId = reference.id
        this.referenceClass = reference.className
    }

    companion object {
        fun of(attachmentDTO: AttachmentDTO): Attachment {
            return Attachment(

            )
        }
    }
}