package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import java.util.*

class DishQueries(
    private val dishRepository: DishRepository,
) {
    inner class FetchDishQ{
        operator fun invoke(id:Int):Dish?{
            return dishRepository.fetch(id)
        }
    }
    inner class DishesOfRestaurantQ {
        operator fun invoke(id: Int):List<Dish>{
            return dishRepository.list().filter {it.restaurant.id == id}
        }
    }
    inner class AddDishQ{
        operator fun invoke(
            currentDishRestaurant : Restaurant,
            currentDishName : String,
            currentDishVegan : Boolean,
            currentDishIngredients : String,
            currentDishDescription : String,
            currentDishAvailability : Boolean,
            currentDishPrice : Int,
            currentDishImageUrl : String,
        ) {
            dishRepository.add(
                Dish {
                    restaurant = currentDishRestaurant
                    dishName = currentDishName
                    vegan = currentDishVegan
                    ingredients = currentDishIngredients
                    dishDescription = currentDishDescription
                    availability = currentDishAvailability
                    price = currentDishPrice
                    imageUrl = currentDishImageUrl
                }
            )
        }
    }
     inner class DeleteDishQ{
         operator fun invoke(dish: Dish) {
             dishRepository.delete(dish.id)
         }
     }

    inner class EditDishQ{
        operator fun invoke(
            nameDish: String,
            ingredients: String,
            price: Int,
            description: String,
            vegan: Boolean,
            availability: Boolean,
            imageUrl: String,
            dish: Dish) {
            dishRepository.changeDish(
                nameDish,
                ingredients,
                vegan,
                description,
                availability,
                price,
                imageUrl,
                dish
            )
        }
    }
    inner class EditAvailability{
        operator fun invoke(dish : Dish){
            dishRepository.editAvailability(dish)
        }
    }
    inner class FilterByNameQ{
        operator fun invoke(nameDish: String?=null,id: Int,flag: Int?,minPrice: Int?,maxPrice: Int?):List<Dish>{
            val name=nameDish?: return FilterByPriceQ().invoke(flag,id, minPrice, maxPrice)
            return dishRepository.list().filter { it.dishName == name && it.restaurant.id == id }
        }
    }
    inner class FilterByPriceQ{
        operator fun invoke(flag:Int?,id: Int,minPrice:Int?,maxPrice:Int?):List<Dish>{
            val priceComparator = Comparator { dish1: Dish, dish2: Dish -> dish1.price - dish2.price }
            val price=flag?: return dishRepository.list().filter {it.restaurant.id == id}
            if(price==0){
                return dishRepository.list().sortedWith(priceComparator).filter {
                    it.restaurant.id == id && it.price >= (minPrice ?: 0) && it.price <= (maxPrice ?: 99999)
                }
            }
            if(price==1) {
                return dishRepository.list().sortedWith(priceComparator).reversed().filter {
                    it.restaurant.id == id && it.price >= (minPrice ?: 0) && it.price <= (maxPrice ?: 99999)
                }
            }
                return dishRepository.list().filter {
                    it.restaurant.id == id && it.price >= (minPrice ?: 0) && it.price <= (maxPrice ?: 99999)
                }
        }
    }
}
