package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Dish
import ru.ac.uniyar.domain.Order
import ru.ac.uniyar.domain.Restaurant
import ru.ac.uniyar.domain.User

data class OperatorOrderVM(
    val order: Order,
    val dishes: List<Dish>,
    val price: Int,
    val restaurant: Restaurant,
    val user: User,
    ): ViewModel
