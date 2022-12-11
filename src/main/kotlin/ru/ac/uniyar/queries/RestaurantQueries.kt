package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Restaurant
import ru.ac.uniyar.domain.RestaurantRepository
import ru.ac.uniyar.domain.Store

class RestaurantQueries(
    private val restaurantRepository: RestaurantRepository,
    private val store: Store,
) {
    inner class FetchRestaurantViaId {
        operator fun invoke(id: Int): Restaurant? {
            return restaurantRepository.fetch(id)
        }
    }
    inner class ListOfRestaurantsQuery {
        operator fun invoke(): List<Restaurant> {
            return restaurantRepository.list()
        }
    }
    inner class DeleteRestaurantQuery {
        operator fun invoke(id: Int){
            store.restaurantRepository.delete(id)
        }
    }
    inner class EditRestaurantQuery {
        operator fun invoke(nameRestaurant: String, restaurant: Restaurant) {
            restaurantRepository.changeRestaurantName(nameRestaurant, restaurant)
        }
    }
    inner class AddRestaurantQuery{
        operator fun invoke(nameRestaurant: String, logoUrl: String) {
            restaurantRepository.add(
                nameRestaurant,
                logoUrl
            )
        }
    }

}