package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Dish
import ru.ac.uniyar.domain.Store

class DeleteDishQuery (private val store: Store) {

    operator fun invoke(dish: Dish) {
        store.dishRepository.delete(dish)
        store.save()
    }
}