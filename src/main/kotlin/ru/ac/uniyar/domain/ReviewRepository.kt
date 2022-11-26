package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import ru.ac.uniyar.database.Reviews

class ReviewRepository(
    database: Database
) {
    private val db = database

    fun fetch(id: Int): Review {
        return db
            .from(Reviews)
            .select()
            .where{ Reviews.id.toInt() eq id }
            .map { Review(
                it.getInt(1),
                it.getInt(2),
                it.getInt(3),
                it.getString(4)!!,
                it.getInt(5),
                it.getLocalDateTime(6)!!
            ) }.first()
    }

    fun add(review: Review): Int {
        return db.insertAndGenerateKey(Reviews) {
            set(it.user_id, review.userId)
            set(it.restaurant_id, review.restaurantId)
            set(it.text, review.text)
            set(it.rating, review.rating)
            set(it.datetime, review.datetime)
        }.toString().toInt()
    }

    fun delete(id: Int) {
        db.delete(Reviews) { it.id eq id }
    }

    fun edit(review: Review){
        db.update(Reviews){
            set(it.user_id, review.userId)
            set(it.restaurant_id, review.restaurantId)
            set(it.text, review.text)
            set(it.rating, review.rating)
            set(it.datetime, review.datetime)
            where {
                it.id eq review.id
            }
        }
    }

    fun list() : List<Review> {
        return  db.from(Reviews).select().map {
            Review(
                it.getInt(1),
                it.getInt(2),
                it.getInt(3),
                it.getString(4)!!,
                it.getInt(5),
                it.getLocalDateTime(6)!!
            )
        }
    }
}