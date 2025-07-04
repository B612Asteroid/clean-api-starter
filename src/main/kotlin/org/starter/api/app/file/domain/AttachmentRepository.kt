package org.starter.api.app.file.domain

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * @author gibeomsong
 * @email
 * @create date 2025-07-04 20:06:185
 * @modify date 2025-07-04 20:06:185
 * @desc 업로드 폴더 Repository
 */
@Repository
interface AttachmentRepository : CrudRepository<Attachment, Long> {
    
}