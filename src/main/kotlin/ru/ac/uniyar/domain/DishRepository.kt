package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*

class DishRepository(
    database: Database
) {
    private val db = database

    fun fetch(id: Int): Dish? {
        return db.dishes.find { it.id eq id }
    }

    fun add(dish: Dish): Int {
        return db.dishes.add(dish)
    }

    fun delete(id: Int) {
        db.delete(OrderDishes) {it.dish_id eq id}
        db.delete(Dishes) { it.id eq id }
    }

    fun changeDish(
        dishName: String,
        ingredients: String,
        vegan: Boolean,
        dishDescription: String,
        availability: Boolean,
        price: Int,
        imageUrl: String,
        dish: Dish
    ) {
        val dishToEdit = db.dishes.find { it.id eq dish.id } ?: return
        dishToEdit.dishName = dishName
        dishToEdit.ingredients = ingredients
        dishToEdit.vegan = vegan
        dishToEdit.dishDescription = dishDescription
        dishToEdit.availability = availability
        dishToEdit.price = price
        dishToEdit.imageUrl = imageUrl
        dishToEdit.flushChanges()
    }

    fun editAvailability(dish : Dish){
        val dishToChange = db.dishes.find { it.id eq dish.id } ?: return
        if (dish.availability){
            dishToChange.availability = false
            val orders = db.order_dishes
                .filter { it.dish_id eq dish.id }
                .map { orderDish -> db.orders.find { it.id eq orderDish.orderId }!!}
                .filter { it.orderStatus == "В ожидании" }
            orders.forEach { order -> db.delete(OrderDishes) { it.dish_id eq dish.id and(it.order_id eq order.id)} }
            dishToChange.flushChanges()
        } else {
            dishToChange.availability = true
            dishToChange.flushChanges()
        }
    }

    fun list(): List<Dish> {
        return db.dishes.toList()
    }


}