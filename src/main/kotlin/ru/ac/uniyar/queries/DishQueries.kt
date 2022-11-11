package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Dish
import ru.ac.uniyar.domain.DishRepository

import java.util.*


interface DishQuery {
    fun fetchDishViaId(id: UUID): Dish
    fun fetchNameDishViaId(id: UUID): String
    fun listOfDishes(id: UUID):List<Dish>
}

class DishQueries(
    private val dishRepository: DishRepository
):DishQuery {
    override fun fetchDishViaId(id: UUID): Dish {
        return dishRepository.fetch(id)!!
    }

    override fun fetchNameDishViaId(id: UUID): String {
        return dishRepository.fetch(id)!!.nameDish
    }

    override fun listOfDishes(id: UUID): List<Dish> {
        return dishRepository.list().filter {it.restaurant_id==id}
    }
}
