package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.Review
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.ShowListOfReviewsVM
import ru.ac.uniyar.models.ShowReviewFormVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.RestaurantQueries
import ru.ac.uniyar.queries.ReviewQueries

import java.time.LocalDateTime
import java.util.*

fun showReviewList(
    permissionsLens: RequestContextLens<RolePermissions>,
    reviewQueries: ReviewQueries,
    restaurantQueries: RestaurantQueries,
    htmlView: ContextAwareViewRender,
    ):HttpHandler = handler@{ request->
    val restaurantIdString = request.path("restaurant").orEmpty()
    val id = UUID.fromString(restaurantIdString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQueries.FetchRestaurantViaId().invoke(id) ?: return@handler Response(Status.BAD_REQUEST)
    val permissions = permissionsLens(request)
    if (!permissions.listReviews)
        Response(Status.UNAUTHORIZED)
    val reviews = reviewQueries.ListOfReviews().invoke().filter { it.restaurantId==restaurant.id }
    Response(Status.OK).with(
        htmlView(request) of ShowListOfReviewsVM(reviews,restaurant)
    )
}
val textReviewFormLens = FormField.string().required("text")
val ratingReviewFormLens = FormField.int().required("rating")
val BodyReviewFormLens = Body.webForm(
    Validator.Feedback,
    textReviewFormLens,
).toLens()

fun showReviewForm(
    permissionsLens: RequestContextLens<RolePermissions>,
    reviewQueries: ReviewQueries,
    htmlView: ContextAwareViewRender,
):HttpHandler = handler@ {request ->

    val permissions = permissionsLens(request)
    if (!permissions.createReview)
        Response(Status.UNAUTHORIZED)
    Response(Status.OK).with(htmlView(request) of ShowReviewFormVM() )
}

fun addReviewToList(
    permissionsLens: RequestContextLens<RolePermissions>,
    reviewQueries: ReviewQueries,
    restaurantQueries: RestaurantQueries,
    htmlView: ContextAwareViewRender,
):HttpHandler = handler@{request->
    val permissions = permissionsLens(request)
    if (!permissions.createReview)
        Response(Status.UNAUTHORIZED)
    val userId = permissions.id
    val restaurantId = UUID.fromString(request.path("restaurant").orEmpty()) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQueries.FetchRestaurantViaId().invoke(restaurantId) ?: return@handler Response(Status.BAD_REQUEST)
    val webForm = BodyReviewFormLens(request)
    if (webForm.errors.isEmpty()) {
        val review= Review(
            EMPTY_UUID,
            userId,
            restaurant.id,
            textReviewFormLens(webForm),
            ratingReviewFormLens(webForm),
            LocalDateTime.now()
        )
        reviewQueries.AddReview().invoke(review)
        Response(Status.FOUND).header("Location", "/reviews/${restaurant.id}")
    }else{
        Response(Status.OK).with(htmlView(request) of ShowReviewFormVM(webForm) )
    }




}