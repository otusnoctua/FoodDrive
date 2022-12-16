package ru.ac.uniyar.queries

import org.ktorm.dsl.eq
import org.ktorm.entity.averageBy
import org.ktorm.entity.filter
import ru.ac.uniyar.domain.Review
import ru.ac.uniyar.domain.ReviewRepository
import ru.ac.uniyar.domain.Store
import ru.ac.uniyar.domain.reviews

class ReviewQueries(
    private val reviewRepository: ReviewRepository,
){
    val db = Store.db

    inner class ReviewsQ{
        operator fun invoke():List<Review>{
            return reviewRepository.list()
        }
    }

    inner class AddReviewQ{
        operator fun invoke(review: Review){
            val oldReview = reviewRepository.reviewByUserAndRestaurant(review.user.id,review.restaurant.id)
            if (oldReview != null ){
                DeleteReviewQ().invoke(oldReview.id)
            }
            reviewRepository.add(review)
        }
    }
    inner class RatingForRestaurantQ {
        operator fun invoke(id : Int) : Double {
            return db.reviews.filter { it.restaurant_id eq id }.averageBy { it.restaurant_rating } ?: return 0.0
        }
    }
    inner class CheckReviewQ {
        operator fun invoke(userId: Int, restaurantId: Int): Boolean {
            return reviewRepository.list().any { it.user.id == userId && it.restaurant.id == restaurantId }
        }
    }
    inner class DeleteReviewQ {
        operator fun invoke(id: Int) {
            reviewRepository.delete(id)
        }
    }

}