package ru.ac.uniyar.domain

import java.time.LocalDateTime

data class Order(
    val id: Int,
    val clientId: Int,
    val restaurantId: Int,
    val status: String,
    val datetime: LocalDateTime,
    val dishes: List<Int>,
){

    fun addElementToDishes(nameDish: String):Order{
        val mas = this.dishes.toMutableList()
        mas.add(nameDish.toInt())
        return this.copy(dishes = mas)
    }
    fun deleteElementFromDishes(index:Int):Order{
        val mas= this.dishes.toMutableList()
        mas.removeAt(index)
        return this.copy(dishes = mas)
    }

    fun editStatus(status: String):Order{
        return this.copy(status = status)
    }

}
