package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonArray
import java.util.*

class DishRepository(dish: Iterable<Dish> = emptyList()) {
    private val allDishes = dish.associateBy { it.id }.toMutableMap()

    companion object{
        fun fromJson(node: JsonNode) : DishRepository {
            val allDishes = node.map{
                Dish.fromJson(it)
            }
            return DishRepository(allDishes)
        }
    }

    fun asJsonObject(): JsonNode {
        return allDishes.values
            .map{ it.asJsonObject() }
            .asJsonArray()
    }

    fun fetch(id: UUID): Dish? = allDishes[id]

    fun add(dish: Dish): UUID {
        var newId = dish.id
        while (allDishes.containsKey(newId) || newId == EMPTY_UUID){
            newId = UUID.randomUUID()
        }
        allDishes[newId] = dish.setUuid(newId)
        return newId
    }

    fun delete(dish: Dish) {
        allDishes.remove(dish.id)
    }

    fun changeDishName(nameDish: String, dish: Dish){
        allDishes[dish.id] = dish.copy(nameDish = nameDish)
    }

    fun list() = allDishes.values.toList()

    fun update(dish: Dish){
        val id=dish.id
        allDishes[id]= dish
    }
}