package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.RestaurantInfo

class ShowListOfRestaurantsVM(val listOfRestaurants: List<RestaurantInfo>): ViewModel