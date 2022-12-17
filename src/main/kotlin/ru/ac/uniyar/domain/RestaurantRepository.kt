package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*

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
        db.restaurants.add(restaurant)
        return restaurant.id
    }

    fun delete(id: Int){
        db.orders.filter { it.restaurant_id eq id }.forEach { order -> db.delete(Orders) { it.id eq order.id} }
        db.reviews.filter { it.restaurant_id eq id }.forEach { review -> db.delete(Reviews) { it.id eq review.id} }
        db.users.filter { it.restaurant_id eq id }.forEach { user -> db.delete(Users) { it.id eq user.id} }
        db.delete(Restaurants) { it.id eq id }
    }

    fun list() : List<Restaurant> {
        return db.restaurants.toList()
    }

    fun changeRestaurant(nameRestaurant: String,logoUrlRestaurant: String, restaurant: Restaurant){
        val restaurantToChange = db.restaurants.find { it.id eq restaurant.id } ?: return
        restaurantToChange.restaurantName = nameRestaurant
        restaurantToChange.logoUrl = logoUrlRestaurant
        restaurantToChange.flushChanges()
    }
}