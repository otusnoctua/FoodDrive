package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Review
import ru.ac.uniyar.domain.ReviewRepository

interface ReviewQuery{
    fun list():List<Review>
    fun add(review: Review)
}
class ReviewQueries(
    private val reviewRepository: ReviewRepository
):ReviewQuery{
    override fun list(): List<Review> {
        return reviewRepository.list()
    }

    override fun add(review: Review) {
       reviewRepository.add(review)
    }
}