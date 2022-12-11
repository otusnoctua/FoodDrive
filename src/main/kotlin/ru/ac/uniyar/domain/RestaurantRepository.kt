package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.toList

class RestaurantRepository(
    database : Database
) {
    private val db = database

    fun fetch(id: Int): Restaurant? {
        return db.restaurants.find { it.id eq id }
    }

    fun add(nameRestaurant: String, logoUrl: String): Int {
        return db.insertAndGenerateKey(Restaurants) {
            set(it.restaurant_name, nameRestaurant)
            set(it.logo_url, logoUrl)
        }.toString().toInt()
    }

    fun delete(id: Int){
        db.delete(Restaurants) { it.id eq id }
    }

    fun list() : List<Restaurant> {
        return db.restaurants.toList()
    }

    //переписать с поддержкой обновления логотипа (logoUrl)
    fun changeRestaurantName(nameRestaurant: String, restaurant: Restaurant){
        db.update(Restaurants) {
            set(it.restaurant_name, nameRestaurant)
            where {
                it.id eq restaurant.id
            }
        }
    }
}