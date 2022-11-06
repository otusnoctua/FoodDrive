package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Restaurant
import ru.ac.uniyar.domain.RestaurantRepository

class ListOfRestaurantsQuery (private val restaurantRepository: RestaurantRepository) {
    operator fun invoke(): List<Restaurant> {
        return restaurantRepository.list()
    }
}