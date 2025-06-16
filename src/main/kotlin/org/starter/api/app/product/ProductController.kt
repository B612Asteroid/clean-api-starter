package org.starter.api.app.product

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/product")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun getProducts() = productService.getAll()
}