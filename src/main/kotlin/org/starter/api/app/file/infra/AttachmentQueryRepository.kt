package org.starter.api.app.file.infra

import com.querydsl.jpa.impl.JPAQueryFactory
import org.starter.api.app.file.domain.Attachment
import org.starter.api.app.file.domain.QAttachment
import org.starter.api.app.file.domain.UploaderType
import org.starter.api.core.security.SecurityHelper
import org.starter.api.core.util.DateUtil
import org.starter.api.infra.persistence.Persistable
import org.springframework.stereotype.Repository
import java.util.*

/**
 * queryDSL Repository
 */
@Repository
class AttachmentQueryRepository(private val jpaQueryFactory: JPAQueryFactory) {


    /**
     * 객체의 첨부파일들을 가져온다.
     *
     * @param persistable
     * @return
     */
    fun findAttachmentsByReference(persistable: Persistable): MutableList<Attachment?> {
        val attachment = QAttachment.attachment
        return jpaQueryFactory
            .selectFrom<Attachment>(attachment)
            .where(
                attachment.referenceId.eq(persistable.id),
                attachment.referenceClass.eq(persistable.className),
                attachment.deleteYn.eq(false)
            )
            .fetch()
    }

    /**
     * 객체의 첨부파일중, ContentType에 맞는 것 하나만 가져온다.
     *
     * @param persistable
     * @return
     */
    fun findAttachmentByReferenceAndContentType(persistable: Persistable, contentType: UploaderType): Attachment? {
        val attachment = QAttachment.attachment
        return jpaQueryFactory
            .selectFrom(attachment)
            .where(
                attachment.referenceId.eq(persistable.id),
                attachment.referenceClass.eq(persistable.className),
                attachment.deleteYn.eq(false),
                attachment.fileType.eq(contentType)
            )
            .orderBy(attachment.id.desc())
            .limit(1)
            .fetchOne()
    }

    /**
     * 대체텍스트를 업데이트한다.
     *
     * @param id
     * @param altText
     */
    fun updateAltTextByAttachmentId(id: Long, altText: String) {
        val attachment = QAttachment.attachment
        jpaQueryFactory
            .update(attachment)
            .set(attachment.altText, altText)
            .set(attachment.updateDate, DateUtil.currentTimeStamp)
            .set(attachment.updater, SecurityHelper.loginUserId)
            .where(attachment.id.eq(id))
            .execute()
    }


    /**
     * 클래스에 연결되어있는 첨부파일 객체를 모두 삭제한다.
     *
     * @param reference
     */
    fun deleteAttachmentByReference(reference: Persistable) {
        val attachment = QAttachment.attachment
        jpaQueryFactory
            .update(attachment)
            .set(attachment.deleteYn, true)
            .set(attachment.updateDate, DateUtil.currentTimeStamp)
            .set(attachment.updater, SecurityHelper.loginUserId)
            .where(
                attachment.referenceClass.eq(reference.className),
                attachment.referenceId.eq(reference.id)
            )
            .execute()
    }

    /**
     * 클래스에 연결되어있는 첨부파일 객체를 모두 삭제한다.
     *
     * @param reference
     */
    fun deleteAttachmentByReference(reference: Persistable, uploaderType: UploaderType) {
        val attachment = QAttachment.attachment
        jpaQueryFactory.update(attachment)
            .set(attachment.deleteYn, true)
            .set(attachment.updateDate, DateUtil.currentTimeStamp)
            .set(attachment.updater, SecurityHelper.loginUserId)
            .where(
                attachment.referenceClass.eq(reference.className),
                attachment.referenceId.eq(reference.id),
                attachment.fileType.eq(uploaderType)
            )
            .execute()
    }
}
