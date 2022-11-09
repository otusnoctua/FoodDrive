package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.Restaurant
import ru.ac.uniyar.domain.RestaurantRepository
import ru.ac.uniyar.domain.Store

class AddRestaurantQuery (private val restaurantRepository: RestaurantRepository,
                          private val store: Store
){
    operator fun invoke(nameRestaurant: String) {
        restaurantRepository.add(
            Restaurant(
                EMPTY_UUID,
                nameRestaurant
            )
        )
        store.save()
    }
}