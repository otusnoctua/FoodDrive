package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Dish
import ru.ac.uniyar.domain.Order

data class OrderFromBasketVM(val order: Order, val dishes: List<Dish>, val price: Int): ViewModel
