package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import ru.ac.uniyar.domain.*
import ru.ac.uniyar.models.ReviewsVM
import ru.ac.uniyar.models.ReviewFormVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.OrderQueries
import ru.ac.uniyar.queries.RestaurantQueries
import ru.ac.uniyar.queries.ReviewQueries

import java.time.LocalDateTime
import java.util.*

class ReviewsH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val reviewQueries: ReviewQueries,
    private val restaurantQueries: RestaurantQueries,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
    ) : HttpHandler {
    override fun invoke(request: Request): Response {
        val currentUser = curUserLens(request)
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(
            request.path("restaurant")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val permissions = permissionsLens(request)
        if (!permissions.listReviews) {
            return Response(Status.UNAUTHORIZED)
        }
        val reviews = reviewQueries.ReviewsQ().invoke().filter { it.restaurant.id == restaurant.id }
        return Response(Status.OK).with(
            htmlView(request) of ReviewsVM(reviews, restaurant, orderQueries.AcceptedOrdersFromRestaurantQ().invoke(currentUser?.id?:-1, restaurant.id))
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
    private val orderQueries: OrderQueries,
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
        if (!orderQueries.AcceptedOrdersFromRestaurantQ().invoke(currentUser.id, currentRestaurant.id)) {
            return Response(Status.BAD_REQUEST)
        }
        if (reviewQueries.CheckReviewQ().invoke(currentUser.id, currentRestaurant.id)) {
            val reviewToEdit = db.reviews.find { it.user_id eq currentUser.id }?.id!!
            reviewQueries.DeleteReviewQ().invoke(reviewToEdit)
        }
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