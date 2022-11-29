package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.EMPTY_UUID
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
    ):HttpHandler {
    override fun invoke(request: Request): Response {
        val id = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val restaurant =
            restaurantQueries.FetchRestaurantQ().invoke(id) ?: return Response(Status.BAD_REQUEST)
        val permissions = permissionsLens(request)
        if (!permissions.listReviews)
            return Response(Status.UNAUTHORIZED)
        val reviews = reviewQueries.ReviewsQ().invoke().filter { it.restaurantId == restaurant.id }
        return Response(Status.OK).with(
            htmlView(request) of ReviewsVM(reviews, restaurant)
        )
    }
}
val textReviewFormLens = FormField.string().required("text")
val ratingReviewFormLens = FormField.int().required("rating")
val BodyReviewFormLens = Body.webForm(
    Validator.Feedback,
    textReviewFormLens,
).toLens()

class ReviewFormH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val reviewQueries: ReviewQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.createReview)
            return Response(Status.UNAUTHORIZED)
        return Response(Status.OK).with(htmlView(request) of ReviewFormVM())
    }
}

class AddReviewH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val reviewQueries: ReviewQueries,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.createReview || user == null)
            return Response(Status.UNAUTHORIZED)
        val restaurantId =
            UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val restaurant =
            restaurantQueries.FetchRestaurantQ().invoke(restaurantId) ?: return Response(Status.BAD_REQUEST)
        val webForm = BodyReviewFormLens(request)
        if (webForm.errors.isEmpty()) {
            val review = Review(
                EMPTY_UUID,
                user.id,
                restaurant.id,
                textReviewFormLens(webForm),
                ratingReviewFormLens(webForm),
                LocalDateTime.now()
            )
            reviewQueries.AddReviewQ().invoke(review)
            return Response(Status.FOUND).header("Location", "/reviews/${restaurant.id}")
        } else {
            return Response(Status.OK).with(htmlView(request) of ReviewFormVM(webForm))
        }
    }
}