package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Restaurant
import ru.ac.uniyar.domain.Review

data class ReviewsVM(
    val listOfReviews : List<Review> ,
    val restaurant: Restaurant,
): ViewModel
