package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import ru.ac.uniyar.database.Restaurants

class RestaurantRepository(
    database : Database
) {
    private val db = database

    fun fetch(id: Int): Restaurant {
        val name = db
            .from(Restaurants)
            .select(Restaurants.name)
            .where { Restaurants.id eq id }
            .toString()
        return Restaurant(id, name)
    }

    fun add(nameRestaurant: String): Int {
        return db.insertAndGenerateKey(Restaurants) {
            set(it.name, nameRestaurant)
        }.toString().toInt()
    }

    fun delete(id: Int){
        db.delete(Restaurants) { it.id eq id }
    }

    fun list() : List<Restaurant> {
        return db.from(Restaurants).select().map {
            Restaurant(
                it.getInt(1),
                it.getString(2)!!
            )
        }
    }

    fun changeRestaurantName(nameRestaurant: String, restaurant: Restaurant){
        db.update(Restaurants) {
            set(it.name, nameRestaurant)
            where {
                it.id eq restaurant.id
            }
        }
    }
}