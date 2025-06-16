package org.starter.api.app.order.domain.model

data class Order(
    val id: Long,
    val productIds: List<Long>,
    val totalPrice: Int
)