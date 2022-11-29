package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.RestaurantInfo

class RestaurantsVM(
    val listOfRestaurants: List<RestaurantInfo>
    ): ViewModel