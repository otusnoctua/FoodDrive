package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.toList

class RestaurantRepository(
    database : Database
) {
    private val db = database

    fun fetch(id: Int): Restaurant? {
        return db.restaurants.find { it.id eq id }
    }

    fun add(nameRestaurant: String, logoUrlRestaurant: String): Int {
        val restaurant = Restaurant {
            restaurantName = nameRestaurant
            logoUrl = logoUrlRestaurant
        }
        return db.restaurants.add(restaurant)
    }

    fun delete(id: Int){
        db.delete(Restaurants) { it.id eq id }
    }

    fun list() : List<Restaurant> {
        return db.restaurants.toList()
    }

    fun changeRestaurantName(nameRestaurant: String, restaurant: Restaurant){
        val restaurantToRename = db.restaurants.find { it.id eq restaurant.id } ?: return
        restaurantToRename.restaurantName = nameRestaurant
        restaurantToRename.flushChanges()
    }

    fun changeRestaurantLogo(logoUrlRestaurant: String, restaurant: Restaurant){
        val restaurantToChange = db.restaurants.find { it.id eq restaurant.id } ?: return
        restaurantToChange.restaurantName = logoUrlRestaurant
        restaurantToChange.flushChanges()
    }
}