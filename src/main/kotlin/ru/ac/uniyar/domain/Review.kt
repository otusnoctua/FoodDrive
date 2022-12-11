package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.LocalDateTime

interface Review : Entity<Review> {
    companion object : Entity.Factory<Review>()
    val id: Int
    var user: User
    var restaurant: Restaurant
    var reviewText: String
    var restaurantRating: Int
    var addTime: LocalDateTime
}

object Reviews : Table<Review>("reviews"){
    val id = int("id").primaryKey().bindTo { it.id }
    val user_id = int("user_id").references(Users) {it.user}
    val restaurant_id = int("restaurant_id").references(Restaurants) {it.restaurant}
    val review_text = text("review_text").bindTo { it.reviewText }
    val restaurant_rating = int("restaurant_rating").bindTo { it.restaurantRating }
    val add_time = datetime("add_time").bindTo { it.addTime }
}

val Database.reviews get() = this.sequenceOf(Reviews)