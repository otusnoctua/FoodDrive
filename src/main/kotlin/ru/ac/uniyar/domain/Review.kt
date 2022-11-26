package ru.ac.uniyar.domain

import java.time.LocalDateTime

data class Review(
    val id: Int,
    val userId: Int,
    val restaurantId: Int,
    val text: String,
    val rating: Int,
    val datetime: LocalDateTime,
)