package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Dish
import ru.ac.uniyar.domain.Order
import ru.ac.uniyar.domain.OrderInfo

data class BasketVM(
    val orders: List<OrderInfo>,
    ):ViewModel
