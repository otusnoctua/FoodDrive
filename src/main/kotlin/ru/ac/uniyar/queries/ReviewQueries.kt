package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Review
import ru.ac.uniyar.domain.ReviewRepository
import ru.ac.uniyar.domain.Store


class ReviewQueries(
    private val reviewRepository: ReviewRepository,
    private val store:Store){

    inner class ListOfReviews{
        operator fun invoke():List<Review>{
            return reviewRepository.list()
        }
    }

    inner class AddReview{
        operator fun invoke(review: Review){
            reviewRepository.add(review)
            store.save()

        }
    }

}