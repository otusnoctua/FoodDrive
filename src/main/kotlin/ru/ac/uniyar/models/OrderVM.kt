package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Order
import ru.ac.uniyar.domain.User

data class OrderVM(
    val order: Order,
    val user: User,
    val ThisIsBasket:Boolean): ViewModel
