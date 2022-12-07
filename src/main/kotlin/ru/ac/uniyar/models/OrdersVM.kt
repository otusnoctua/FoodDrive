package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Dish
import ru.ac.uniyar.domain.Order

data class OrdersVM(val newOrders: List<Order>, val oldOrders: List<Order> = emptyList() ):ViewModel
