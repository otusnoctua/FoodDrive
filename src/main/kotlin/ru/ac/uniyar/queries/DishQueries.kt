package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*

class DishQueries(
    private val dishRepository: DishRepository,
    private val store: Store
) {
    inner class FetchDishViaId{
        operator fun invoke(id: Int) : Dish?{
            return dishRepository.fetch(id)
        }
    }
    inner class FetchNameDishViaId{
        operator fun invoke(id: Int): String {
            return dishRepository.fetch(id)?.name ?: ""
        }
    }
    inner class ListOfDishes {
        operator fun invoke(id: Int):List<Dish>{
            return dishRepository.list().filter {it.restaurantId==id}
        }
    }
    inner class AddDishQuery{
        operator fun invoke(restaurant: Restaurant, nameDish: String, ingredients: String, vegan: Boolean, description: String ){
            dishRepository.add(
                Dish(
                    0,
                    nameDish,
                    restaurant.id,
                    ingredients,
                    vegan,
                    description,
                )
            )
        }
    }
     inner class DeleteDishQuery{

         operator fun invoke(dish: Dish) {
             store.dishRepository.delete(dish.id)
         }
     }

    inner class EditDishQuery{
        operator fun invoke(
            name: String,
            ingredients: String,
            vegan: Boolean,
            description: String,
            dish: Dish) {
            dishRepository.edit(name, ingredients, vegan, description, dish)
        }
    }
}
