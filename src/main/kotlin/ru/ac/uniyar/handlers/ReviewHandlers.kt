package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.Review
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.domain.User
import ru.ac.uniyar.models.ShowListOfReviewsVM
import ru.ac.uniyar.models.ShowReviewFormVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.RestaurantQueries
import ru.ac.uniyar.queries.ReviewQueries

import java.time.LocalDateTime

class ShowReviewList(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val reviewQueries: ReviewQueries,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
    ):HttpHandler {
    override fun invoke(request: Request): Response {
        val id = request.path("restaurant")!!.toInt()
        val restaurant =
            restaurantQueries.FetchRestaurantViaId().invoke(id)
        val permissions = permissionsLens(request)
        if (!permissions.listReviews)
            return Response(Status.UNAUTHORIZED)
        val reviews = reviewQueries.ListOfReviews().invoke().filter { it.restaurant.id == restaurant!!.id }
        if (restaurant != null) {
            return Response(Status.OK).with(
            htmlView(request) of ShowListOfReviewsVM(reviews, restaurant))
        } else {
            return Response(Status.BAD_REQUEST)
        }
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
    private val curUserLens: RequestContextLens<User?>,
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val reviewQueries: ReviewQueries,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val curUser = curUserLens(request)
        if (!permissions.createReview || curUser == null)
            return Response(Status.UNAUTHORIZED)
        val userId = curUser.id
        val reviewedRestaurantId = request.path("restaurant")!!.toInt()
        val reviewedRestaurant = restaurantQueries.FetchRestaurantViaId().invoke(reviewedRestaurantId)
        val webForm = BodyReviewFormLens(request)
        if (webForm.errors.isEmpty()) {
            val review = Review {
                user = curUser
                if (reviewedRestaurant != null) {
                    restaurant = reviewedRestaurant
                }
                reviewText = textReviewFormLens(webForm)
                restaurantRating = ratingReviewFormLens(webForm)
                addTime = LocalDateTime.now()
            }
            reviewQueries.AddReview().invoke(review)
            return Response(Status.FOUND).header("Location", "/reviews/${reviewedRestaurant!!.id}")
        } else {
            return Response(Status.OK).with(htmlView(request) of ShowReviewFormVM(webForm))
        }
    }
}