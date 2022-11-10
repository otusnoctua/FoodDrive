package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*

class AddDishQuery (
    private val dishRepository: DishRepository,
    private val store: Store
) {

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