package org.starter.api.app.product

import org.springframework.stereotype.Service

@Service
class ProductService {
    fun getAll(): List<Product> {
        return listOf(
            Product(1, "Sample", 1000)
        )
    }
}