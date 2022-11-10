package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Dish
import ru.ac.uniyar.domain.DishRepository
import ru.ac.uniyar.domain.Store

class EditDishQuery (private val store: Store,
                     private val dishRepository: DishRepository
) {
    operator fun invoke(nameDish: String, dish: Dish) {
        dishRepository.changeDishName(nameDish, dish)
        store.save()
    }
}