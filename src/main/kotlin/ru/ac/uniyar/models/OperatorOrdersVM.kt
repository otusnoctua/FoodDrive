package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Order

data class OperatorOrdersVM( val orders: List<Order>): ViewModel
