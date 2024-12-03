package com.sebstreb.rank_anything.backend.models.dtos

data class ChangePassword(
    val oldPassword: String,
    val newPassword: String,
)
