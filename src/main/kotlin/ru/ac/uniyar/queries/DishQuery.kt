package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Dish
import ru.ac.uniyar.domain.DishRepository
import java.util.*

class DishQuery (private val dishRepository: DishRepository) {

    operator fun invoke(id: UUID): Dish? {
        return dishRepository.fetch(id)
    }
}