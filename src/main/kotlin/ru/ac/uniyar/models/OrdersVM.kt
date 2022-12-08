package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Order

data class OrdersVM(
    val newOrders: List<Order> = emptyList(),
    val oldOrders: List<Order> = emptyList(),
    ):ViewModel
