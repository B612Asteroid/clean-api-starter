package org.starter.api.app.product

import jakarta.persistence.*
import org.starter.api.infra.persistence.Persistable

@Entity
class Product: Persistable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null
        protected set

    @Column
    var name: String = ""
        protected set

    @Column
    var price: Int = 0
        protected set

    constructor(id: Long, name: String, price: Int) {
        this.id = id
        this.name = name
        this.price = price
    }
}