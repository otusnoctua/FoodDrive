package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*

import java.util.*

class DishQueries(
    private val dishRepository: DishRepository,
    private val store: Store
) {
    inner class FetchDishQ{
        operator fun invoke(id:UUID):Dish?{
            return dishRepository.fetch(id)
        }
    }
    inner class DishesOfRestaurantQ {
        operator fun invoke(id: UUID):List<Dish>{
            return dishRepository.list().filter {it.restaurantId == id}
        }
    }
    inner class AddDishQ{
        operator fun invoke(restaurant: Restaurant, nameDish: String, ingredients: String, price: Int, vegan: Boolean, description: String ){
            dishRepository.add(
                Dish(
                    EMPTY_UUID,
                    restaurant.id,
                    ingredients,
                    price,
                    vegan,
                    description,
                    nameDish,
                    true
                )
            )
            store.save()
        }
    }
     inner class DeleteDishQ{
         operator fun invoke(dish: Dish) {
             store.dishRepository.delete(dish)
             store.save()
         }
     }

    inner class EditDishQ{
        operator fun invoke(nameDish: String, ingredients: String, price: Int, description: String, vegan: Boolean, dish: Dish) {
            dishRepository.changeDish(nameDish, ingredients, price, description, vegan, dish)
            store.save()
        }
    }
    inner class EditAvailability{
        operator fun invoke(dish:Dish){
            dishRepository.update(dish.editAvailability())
            store.save()
        }
    }
    inner class FilterByNameQ{
        operator fun invoke(nameDish: String?=null,id: UUID,flag: Int?,minPrice: Int?,maxPrice: Int?):List<Dish>{
            val name=nameDish?: return FilterByPriceQ().invoke(flag,id, minPrice, maxPrice)
            return dishRepository.list().filter { it.name==name && it.restaurantId == id }
        }
    }
    inner class FilterByPriceQ{
        operator fun invoke(flag:Int?,id: UUID,minPrice:Int?,maxPrice:Int?):List<Dish>{
            val priceComparator = Comparator { dish1: Dish, dish2: Dish -> dish1.price - dish2.price }
            val price=flag?: return dishRepository.list().filter {it.restaurantId == id}
            if(price==0){
                return dishRepository.list().sortedWith(priceComparator).filter {
                    it.restaurantId == id && it.price >= (minPrice ?: 0) && it.price <= (maxPrice ?: 99999)
                }
            }
            if(price==1) {
                return dishRepository.list().sortedWith(priceComparator).reversed().filter {
                    it.restaurantId == id && it.price >= (minPrice ?: 0) && it.price <= (maxPrice ?: 99999)
                }
            }
                return dishRepository.list().filter {
                    it.restaurantId == id && it.price >= (minPrice ?: 0) && it.price <= (maxPrice ?: 99999)
                }
        }
    }
}
