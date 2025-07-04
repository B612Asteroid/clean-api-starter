package org.starter.api.app.file.infra

import org.starter.api.app.file.service.AttachmentService
import org.starter.api.infra.persistence.Persistable
import org.starter.api.infra.persistence.PersistenceService
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 어떤 데이터가 저장되었을 때 업로드 파일이 존재하면 해당 데이터도 같이 지워주는 Aspect
 */
@Component
@Aspect
class AttachmentAspect(
    private val persistenceService: PersistenceService,
    private val attachmentService: AttachmentService
) {
    /**
     * 교과서가 새로 생성되었을 때, 교과 목차 + AI 목차를 자동으로 생성해준다.)
     *
     * @param jp
     * @throws Throwable
     */
    @After(value = "@annotation(org.starter.api.app.file.infra.AttachmentDeleted)")
    @Transactional(rollbackFor = [Exception::class])
    fun aspectDeleteAttachment(jp: JoinPoint) {
        log.debug("aspectDeleteAttachment JoinPoint START >>> {}", jp.target)

        val objects: Array<Any?> = jp.args
        val `object` = objects[0]
        if (`object` is MutableList<*> && `object`[0] is String) {
            for (oid in `object`) {
                this.deleteAttachment(oid.toString())
            }
        } else if (`object` is String) {
            this.deleteAttachment(`object`)
        }

        log.debug("checkToken JoinPoint END")
    }


    /**
     * 실제 첨부파일 삭제
     *
     * @param oid
     */
    private fun deleteAttachment(oid: String) {
        val persistable: Persistable =  persistenceService.getReference(oid)
        attachmentService.deleteAttachmentsByReference(persistable)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AttachmentAspect::class.java)
    }
}
