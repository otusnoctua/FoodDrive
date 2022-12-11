package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.add
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
        return db.reviews.add(review)
    }

    fun delete(id: Int) {
        val review = db.reviews.find { it.id eq id } ?: return
        review.delete()
    }

    fun edit(review: Review){
        val reviewToEdit = db.reviews.find { it.id eq review.id } ?: return
        reviewToEdit.user = review.user
        reviewToEdit.restaurant = review.restaurant
        reviewToEdit.reviewText = review.reviewText
        reviewToEdit.restaurantRating = review.restaurantRating
        reviewToEdit.addTime = review.addTime
        reviewToEdit.flushChanges()
    }

    fun list() : List<Review> {
        return db.reviews.toList()
    }
}