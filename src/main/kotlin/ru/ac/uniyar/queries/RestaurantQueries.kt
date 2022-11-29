package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.Restaurant
import ru.ac.uniyar.domain.RestaurantRepository
import ru.ac.uniyar.domain.Store
import java.util.*

class RestaurantQueries(
    private val restaurantRepository: RestaurantRepository,
    private val store: Store,
) {
    inner class FetchRestaurantQ {
        operator fun invoke(id: UUID): Restaurant? {
            return restaurantRepository.fetch(id)
        }
    }
    inner class RestaurantsQ {
        operator fun invoke(): List<Restaurant> {
            return restaurantRepository.list()
        }
    }
    inner class DeleteRestaurantQ {
        operator fun invoke(id:UUID){
            store.restaurantRepository.delete(id)
            store.save()
        }
    }
    inner class EditRestaurantQ {
        operator fun invoke(nameRestaurant: String, restaurant: Restaurant) {
            restaurantRepository.changeRestaurantName(nameRestaurant, restaurant)
            store.save()
        }
    }
    inner class AddRestaurantQ{
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

}