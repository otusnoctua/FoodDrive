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
            return dishRepository.fetch(id)?.dishName ?: ""
        }
    }
    inner class ListOfDishes {
        operator fun invoke(id: Int):List<Dish>{
            return dishRepository.list().filter { it.restaurant.id == id }
        }
    }
    inner class AddDishQuery{
        operator fun invoke(
            currentDishRestaurant: Restaurant,
            currentDishName: String,
            currentDishIngredients: String,
            currentDishVegan: Boolean,
            currentDishDescription: String,
            currentDishAvailability: Boolean,
            currentDishPrice: Int,
            currentDishImageUrl: String
        ) {

            val dish = Dish {
                dishName = currentDishName
                restaurant = currentDishRestaurant
                ingredients = currentDishIngredients
                vegan = currentDishVegan
                dishDescription = currentDishDescription
                availability = currentDishAvailability
                price = currentDishPrice
                imageUrl = currentDishImageUrl
            }

            dishRepository.add(dish)
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
            dishDescription: String,
            availability: Boolean,
            price: Int,
            imageUrl: String,
            dish: Dish
        ) {
            dishRepository.edit(
                name,
                ingredients,
                vegan,
                dishDescription,
                availability,
                price,
                imageUrl,
                dish
            )
        }
    }
}
