package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Dish
import ru.ac.uniyar.domain.Restaurant

data class RestaurantVM(
    val dishes: List<Dish>,
    val restaurant: Restaurant,
    ): ViewModel