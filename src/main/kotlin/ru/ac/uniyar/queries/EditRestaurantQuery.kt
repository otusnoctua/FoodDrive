package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Restaurant
import ru.ac.uniyar.domain.RestaurantRepository
import ru.ac.uniyar.domain.Store

class EditRestaurantQuery (private val store: Store,
                           private val restaurantRepository: RestaurantRepository
) {
    operator fun invoke(nameRestaurant: String, restaurant: Restaurant) {
        restaurantRepository.changeRestaurantName(nameRestaurant, restaurant)
        store.save()
    }
}