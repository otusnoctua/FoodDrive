package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Review
import ru.ac.uniyar.domain.ReviewRepository


class ReviewQueries(
    private val reviewRepository: ReviewRepository){

    inner class ListOfReviews{
        operator fun invoke():List<Review>{
            return reviewRepository.list()
        }
    }

    inner class AddReview{
        operator fun invoke(review: Review){
            reviewRepository.add(review)
        }
    }

}