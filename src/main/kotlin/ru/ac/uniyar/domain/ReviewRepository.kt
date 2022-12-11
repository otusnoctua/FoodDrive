package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.toList

class ReviewRepository(
    database: Database
) {
    private val db = database

    fun fetch(id: Int): Review? {
        return db.reviews.find { it.id eq id}
    }

    fun add(review: Review): Int {
        return db.insertAndGenerateKey(Reviews) {
            set(it.user_id, review.user.id)
            set(it.restaurant_id, review.restaurant.id)
            set(it.review_text, review.reviewText)
            set(it.restaurant_rating, review.restaurantRating)
            set(it.add_time, review.addTime)
        }.toString().toInt()
    }

    fun delete(id: Int) {
        db.delete(Reviews) { it.id eq id }
    }

    fun edit(review: Review){
        db.update(Reviews){
            set(it.user_id, review.user.id)
            set(it.restaurant_id, review.restaurant.id)
            set(it.review_text, review.reviewText)
            set(it.restaurant_rating, review.restaurantRating)
            set(it.add_time, review.addTime)
            where {
                it.id eq review.id
            }
        }
    }

    fun list() : List<Review> {
        return db.reviews.toList()
    }
}