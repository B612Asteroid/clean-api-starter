package org.starter.api.app.order.infra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.starter.api.app.order.domain.model.Order

@Repository
interface OrderRepository: JpaRepository<Order, Long> {
}