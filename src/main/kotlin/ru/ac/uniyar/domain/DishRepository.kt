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

    fun changeDish(nameDish: String, ingredients: String, price: Int, description: String, vegan: Boolean, dish: Dish){
        allDishes[dish.id] = dish.copy(nameDish = nameDish, ingredients = ingredients, price = price, description = description, vegan = vegan)
    }

    fun list() = allDishes.values.toList()
}