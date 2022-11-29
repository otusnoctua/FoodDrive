package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Dish
import ru.ac.uniyar.domain.Restaurant

class RestaurantVM(
    val listOfDishes: List<Dish>,
    val restaurant: Restaurant,
    ): ViewModel