package ru.ac.uniyar.domain

data class User(
    val id: Int,
    val name: String,
    val phone: Long,
    val email: String,
    val password: String,
    val roleId: Int,
)
