package org.starter.api.app.file.service

import org.starter.api.app.file.infra.AttachmentQueryRepository
import org.starter.api.app.file.domain.AttachmentRepository
import org.starter.api.core.AppProperties
import org.starter.api.core.MessageSourceWrapper
import org.starter.api.infra.persistence.PersistenceService
import org.starter.api.infra.s3.S3ClientService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/**
 * @author gsong
 * @email gsong@chunjae.co.kr
 * @create date 2025-05-19 07:39:139
 * @modify date 2025-05-19 07:39:139
 * @desc 파일 서비스에서 사용되는 디팬던시 Assembler
 */
@Component
class AttachmentDependencies(
    val attachmentRepository: AttachmentRepository,
    val attachmentQueryRepository: AttachmentQueryRepository,
    val s3ClientServicePart: S3ClientService,  /* 생성자에는 이렇게 Lazy로 명시해줘야 순환참조를 피할 수 있음.*/
    @Lazy val self: AttachmentService,
    val messageSourceWrapper: MessageSourceWrapper,
    val persistenceService: PersistenceService,
    val appProperties: AppProperties
)