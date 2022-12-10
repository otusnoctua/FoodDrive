package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.RestaurantInfo

data class RestaurantsVM(
    val listOfRestaurants: List<RestaurantInfo>,
    val name:String?,
    ): ViewModel