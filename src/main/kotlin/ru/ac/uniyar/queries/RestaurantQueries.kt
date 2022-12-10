package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import java.util.*

class RestaurantQueries(
    private val restaurantRepository: RestaurantRepository,
    private val reviewQueries: ReviewQueries,
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
    inner class FilterByNameQ{
        operator fun invoke(nameRestaurant: String?=null,flag:Int?):List<Restaurant>{
            val name=nameRestaurant?: return FilterByRatingQ().invoke(flag)
            return restaurantRepository.list().filter { it.name==name }
        }
    }
    inner class FilterByRatingQ{
        operator fun invoke(flag:Int?):List<Restaurant>{
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