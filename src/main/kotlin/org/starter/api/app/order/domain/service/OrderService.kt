package org.starter.api.app.order.domain.service

import org.springframework.stereotype.Service
import org.starter.api.app.order.domain.model.Order

@Service
class OrderService {
    fun calculateTotal(order: Order): Int {
        return order.totalPrice
    }
}