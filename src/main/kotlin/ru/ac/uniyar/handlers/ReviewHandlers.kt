package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.Review
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.domain.User
import ru.ac.uniyar.models.ReviewsVM
import ru.ac.uniyar.models.ReviewFormVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.RestaurantQueries
import ru.ac.uniyar.queries.ReviewQueries

import java.time.LocalDateTime
import java.util.*

class ReviewsH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val reviewQueries: ReviewQueries,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
    ) : HttpHandler {
    override fun invoke(request: Request): Response {
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(
            request.path("restaurant")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val permissions = permissionsLens(request)
        if (!permissions.listReviews) {
            return Response(Status.UNAUTHORIZED)
        }
        val reviews = reviewQueries.ReviewsQ().invoke().filter { it.restaurant.id == restaurant.id }
        return Response(Status.OK).with(
            htmlView(request) of ReviewsVM(reviews, restaurant)
        )
    }
}


class ReviewFormH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    companion object{
        val textReviewFormLens = FormField.string().required("text")
        val ratingReviewFormLens = FormField.int().required("rating")
        val BodyReviewFormLens = Body.webForm(
            Validator.Feedback,
            textReviewFormLens,
            ratingReviewFormLens,
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.createReview) {
            return  Response(Status.UNAUTHORIZED)
        }
        return Response(Status.OK).with(
            htmlView(request) of ReviewFormVM()
        )
    }
}

class AddReviewH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val reviewQueries: ReviewQueries,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    companion object{
        val textReviewFormLens = FormField.string().required("text")
        val ratingReviewFormLens = FormField.int().required("rating")
        val BodyReviewFormLens = Body.webForm(
            Validator.Feedback,
            textReviewFormLens,
            ratingReviewFormLens,
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val currentUser = curUserLens(request)
        if (!permissions.createReview || currentUser == null) {
            return Response(Status.UNAUTHORIZED)
        }
        val currentRestaurant = restaurantQueries.FetchRestaurantQ().invoke(
            request.path("restaurant")?.toInt() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val webForm = BodyReviewFormLens(request)
        return if (webForm.errors.isEmpty()) {
            val review = Review {
                user = currentUser
                restaurant = currentRestaurant
                reviewText = textReviewFormLens(webForm)
                restaurantRating = ratingReviewFormLens(webForm)
                addTime = LocalDateTime.now()
            }
            reviewQueries.AddReviewQ().invoke(review)
            Response(Status.FOUND).header(
                "Location", "/reviews/${currentRestaurant.id}"
            )
        } else {
            Response(Status.OK).with(
                htmlView(request) of ReviewFormVM(webForm)
            )
        }
    }
}