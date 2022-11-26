package ru.ac.uniyar.domain

data class Dish(
    val id: Int,
    val name: String,
    val restaurantId : Int,
    val ingredients: String,
    val vegan: Boolean,
    val description: String,
)

