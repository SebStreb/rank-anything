package com.sebstreb.rank_anything.backend.models.dtos

import org.springframework.security.core.authority.SimpleGrantedAuthority

data class PublicUser(
    val id: Long,
    val email: String,
    var role: String,
) {
    fun generateAuthorities() = when (role) {
        "admin" -> listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        else -> emptyList()
    }
}
