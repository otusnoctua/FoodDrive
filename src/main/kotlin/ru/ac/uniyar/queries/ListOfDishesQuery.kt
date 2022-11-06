package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Dish
import ru.ac.uniyar.domain.DishRepository
import java.util.*

class ListOfDishesQuery (private val dishRepository: DishRepository) {
    operator fun invoke(id: UUID): List<Dish> {
        return dishRepository.list().filter {it.restaurant_id==id}
    }
}