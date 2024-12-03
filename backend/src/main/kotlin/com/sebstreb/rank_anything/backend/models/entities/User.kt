package com.sebstreb.rank_anything.backend.models.entities

import com.sebstreb.rank_anything.backend.models.dtos.PublicUser
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    var password: String,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var role: String = "user",
) {
    fun toPublicUser() = PublicUser(id, email, role)
}
