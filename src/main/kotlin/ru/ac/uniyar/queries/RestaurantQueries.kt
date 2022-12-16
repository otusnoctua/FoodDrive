package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import java.util.*

class RestaurantQueries(
    private val restaurantRepository: RestaurantRepository,
    private val reviewQueries: ReviewQueries,
) {
    inner class FetchRestaurantQ {
        operator fun invoke(id: Int): Restaurant? {
            return restaurantRepository.fetch(id)
        }
    }
    inner class RestaurantsQ {
        operator fun invoke(): List<Restaurant> {
            return restaurantRepository.list()
        }
    }
    inner class DeleteRestaurantQ {
        operator fun invoke(restaurant: Restaurant){
            restaurantRepository.delete(restaurant.id)
        }
    }
    inner class EditRestaurantQ {
        operator fun invoke(nameRestaurant: String,logoUrlRestaurant: String, restaurant: Restaurant) {
            restaurantRepository.changeRestaurant(nameRestaurant, logoUrlRestaurant, restaurant)
        }
    }
    inner class AddRestaurantQ{
        operator fun invoke(nameRestaurant: String, logoUrlRestaurant: String) {
            restaurantRepository.add(nameRestaurant, logoUrlRestaurant)
        }
    }
    inner class FilterByNameQ{
        operator fun invoke(
            nameRestaurant: String?=null,
            flag:Int?
        ) : List<Restaurant> {
            val name = nameRestaurant?: return FilterByRatingQ().invoke(flag)
            return restaurantRepository.list().filter { it.restaurantName == name }
        }
    }
    inner class FilterByRatingQ {
        operator fun invoke(flag:Int?) : List<Restaurant> {
            val ratingComparator = Comparator {
                    res1: Restaurant, res2:Restaurant -> (reviewQueries.RatingForRestaurantQ().invoke(res1.id)*100).toInt() -
                    (reviewQueries.RatingForRestaurantQ().invoke(res2.id)*100).toInt() }
            if(flag==0){
                return restaurantRepository.list().sortedWith(ratingComparator)
            }
            if(flag==1){
                return restaurantRepository.list().sortedWith(ratingComparator).reversed()
            }
            return restaurantRepository.list()
        }
    }

    }