package ru.ac.uniyar.queries

import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.averageBy
import org.ktorm.entity.filter
import ru.ac.uniyar.domain.Review
import ru.ac.uniyar.domain.ReviewRepository
import ru.ac.uniyar.domain.Store
import ru.ac.uniyar.domain.reviews

class ReviewQueries(
    private val reviewRepository: ReviewRepository,
    private val store: Store,
    private val database: Database
){
    val db = database

    inner class ReviewsQ{
        operator fun invoke():List<Review>{
            return reviewRepository.list()
        }
    }

    inner class AddReviewQ{
        operator fun invoke(review: Review){
            reviewRepository.add(review)
        }
    }
    inner class RatingForRestaurantQ {
        operator fun invoke(id : Int) : Double {
            return db.reviews.filter { it.restaurant_id eq id }.averageBy { it.restaurant_rating } ?: return 0.0
        }
    }

}