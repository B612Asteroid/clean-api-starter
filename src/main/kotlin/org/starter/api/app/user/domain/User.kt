package org.starter.api.app.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.starter.api.infra.persistence.Persistable

@Entity
@Table(name = "users")
class User() : Persistable() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null

    @Column(nullable = false, unique = true)
    var userId: String = ""
        protected set

    @Column(nullable = false, unique = true)
    var email: String = ""
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.USER
        protected set
}