package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Restaurant
import ru.ac.uniyar.domain.RestaurantRepository
import java.util.*

class RestaurantQuery (private val restaurantRepository: RestaurantRepository) {
    operator fun invoke(id: UUID): Restaurant? {
        return restaurantRepository.fetch(id)
    }
}