package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.Review
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.domain.User
import ru.ac.uniyar.models.ShowListOfReviewsVM
import ru.ac.uniyar.models.ShowReviewFormVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.RestaurantQueries
import ru.ac.uniyar.queries.ReviewQueries

import java.time.LocalDateTime
import java.util.*

class ShowReviewList(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val reviewQueries: ReviewQueries,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
    ):HttpHandler {
    override fun invoke(request: Request): Response {
        val id = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val restaurant =
            restaurantQueries.FetchRestaurantViaId().invoke(id) ?: return Response(Status.BAD_REQUEST)
        val permissions = permissionsLens(request)
        if (!permissions.listReviews)
            return Response(Status.UNAUTHORIZED)
        val reviews = reviewQueries.ListOfReviews().invoke().filter { it.restaurantId == restaurant.id }
        return Response(Status.OK).with(
            htmlView(request) of ShowListOfReviewsVM(reviews, restaurant)
        )
    }
}
val textReviewFormLens = FormField.string().required("text")
val ratingReviewFormLens = FormField.int().required("rating")
val BodyReviewFormLens = Body.webForm(
    Validator.Feedback,
    textReviewFormLens,
).toLens()

class ShowReviewForm(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val reviewQueries: ReviewQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.createReview)
            return Response(Status.UNAUTHORIZED)
        return Response(Status.OK).with(htmlView(request) of ShowReviewFormVM())
    }
}

class AddReviewToList(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val currentUserLens: RequestContextLens<User?>,
    private val reviewQueries: ReviewQueries,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.createReview)
            return Response(Status.UNAUTHORIZED)
        val userId = currentUserLens(request)!!.id
        val restaurantId =
            UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val restaurant =
            restaurantQueries.FetchRestaurantViaId().invoke(restaurantId) ?: return Response(Status.BAD_REQUEST)
        val webForm = BodyReviewFormLens(request)
        if (webForm.errors.isEmpty()) {
            val review = Review(
                EMPTY_UUID,
                userId,
                restaurant.id,
                textReviewFormLens(webForm),
                ratingReviewFormLens(webForm),
                LocalDateTime.now()
            )
            reviewQueries.AddReview().invoke(review)
            return Response(Status.FOUND).header("Location", "/reviews/${restaurant.id}")
        } else {
            return Response(Status.OK).with(htmlView(request) of ShowReviewFormVM(webForm))
        }
    }
}