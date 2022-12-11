package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.toList

class DishRepository(
    //database: Database
) {
    //private val db = database
    val dbWithConn = Database.connect(
        url = "jdbc:mysql://127.0.0.1:3306/fooddrive",
        driver = "com.mysql.jdbc.Driver",
        user = "fda",
        password = "poorsara"
    )


    fun fetch(id: Int): Dish? {
        return db.dishes.find { it.id eq id }
    }

    fun add(dish: Dish): Int {

        db.dishes.add(dish)
        return dish.id

//        return db.insertAndGenerateKey(Dishes) {
//            set(it.dishName, dish.dishName)
//            set(it.restaurant_id, dish.restaurant.id)
//            set(it.ingredients, dish.ingredients)
//            set(it.vegan, dish.vegan)
//            set(it.dishDescription, dish.dishDescription)
//            set(it.availability, dish.availability)
//            set(it.price, dish.price)
//            set(it.imageUrl, dish.imageUrl)
//        }.toString().toInt()
    }

    fun delete(id: Int) {
        db.delete(Dishes) { it.id eq id }
    }

    fun edit(
        dishName: String,
        ingredients: String,
        vegan: Boolean,
        dishDescription: String,
        availability: Boolean,
        price: Int,
        imageUrl: String,
        dish: Dish
    ) {
        db.update(Dishes) {
            set(it.dishName, dishName)
            set(it.ingredients, ingredients)
            set(it.vegan, vegan)
            set(it.dishDescription, dishDescription)
            set(it.availability, availability)
            set(it.price, price)
            set(it.imageUrl, imageUrl)
            where {
                it.id eq dish.id
            }
        }
    }

    fun list(): List<Dish> {
        return db.dishes.toList()
    }


}