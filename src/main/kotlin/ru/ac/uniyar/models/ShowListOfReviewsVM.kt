package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Review

data class ShowListOfReviewsVM(val listOfReviews : List<Review> ): ViewModel
