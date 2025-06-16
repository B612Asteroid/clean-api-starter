package org.starter.api.app.order.ui.controller

import org.starter.api.app.order.domain.service.OrderService

import org.springframework.web.bind.annotation.*
import org.starter.api.app.order.domain.model.Order

@RestController
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    fun placeOrder(@RequestBody order: Order): Int {
        return orderService.calculateTotal(order)
    }
}