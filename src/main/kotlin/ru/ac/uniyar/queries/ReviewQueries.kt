package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Review
import ru.ac.uniyar.domain.ReviewRepository
import ru.ac.uniyar.domain.Store
import java.util.UUID


class ReviewQueries(
    private val reviewRepository: ReviewRepository,
    private val store: Store
){

    inner class ReviewsQ{
        operator fun invoke():List<Review>{
            return reviewRepository.list()
        }
    }

    inner class AddReviewQ{
        operator fun invoke(review: Review){
            reviewRepository.add(review)
            store.save()

        }
    }
    inner class RatingForRestaurantQ{
        operator fun invoke(id:UUID):Double{
            val list= reviewRepository.list().filter { it.restaurantId==id }
                return getAverage(list)
        }
        private fun getAverage(list:List<Review>):Double{
            var sum: Long = 0
            for (i in list) {
                sum += i.rating.toLong()
            }
            return if (list.isNotEmpty()) sum.toDouble() / list.size else 0.0
        }
    }

}