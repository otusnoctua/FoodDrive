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
            return dishRepository.list().filter {it.restaurantId==id}
        }
    }
    inner class AddDishQ{
        operator fun invoke(restaurant: Restaurant, nameDish: String, ingredients: String, vegan: Boolean, description: String ){
            dishRepository.add(
                Dish(
                    EMPTY_UUID,
                    restaurant.id,
                    ingredients,
                    vegan,
                    description,
                    nameDish
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
        operator fun invoke(nameDish: String, ingredients: String, description: String, vegan: Boolean, dish: Dish) {
            dishRepository.changeDish(nameDish, ingredients, description, vegan, dish)
            store.save()
        }
    }
}
