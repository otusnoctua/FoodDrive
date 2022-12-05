package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Review
import ru.ac.uniyar.domain.ReviewRepository
import ru.ac.uniyar.domain.Store


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

}