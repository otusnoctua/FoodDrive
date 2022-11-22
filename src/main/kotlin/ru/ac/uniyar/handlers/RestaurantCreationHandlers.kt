package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.ShowEditRestaurantFormVM
import ru.ac.uniyar.models.ShowRestaurantFormVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.RestaurantQueries
import java.util.*

fun showRestaurantForm(
    permissionLens: RequestContextLens<RolePermissions>,
    htmlView: ContextAwareViewRender,
): HttpHandler = { request ->
    val permissions = permissionLens(request)
    if (!permissions.createRestaurant)
        Response (Status.UNAUTHORIZED)
    Response(Status.OK).with(htmlView(request) of ShowRestaurantFormVM())
}

val restaurantNameFormLens = FormField.string().required("nameRestaurant")
val BodyRestaurantFormLens = Body.webForm(
    Validator.Feedback, restaurantNameFormLens,
).toLens()

fun addRestaurant(
    permissionLens: RequestContextLens<RolePermissions>,
    restaurantQueries: RestaurantQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = { request ->
    val permissions = permissionLens(request)
    if (!permissions.createRestaurant)
        Response (Status.UNAUTHORIZED)
    val webForm = BodyRestaurantFormLens(request)
    if (webForm.errors.isEmpty()) {
        restaurantQueries.AddRestaurantQuery().invoke(restaurantNameFormLens(webForm))
        Response(Status.FOUND).header("Location", "/restaurants")
    } else {
        Response(Status.OK).with(htmlView(request) of ShowRestaurantFormVM(webForm))
    }
}


fun editRestaurant(
    permissionLens: RequestContextLens<RolePermissions>,
    restaurantQueries: RestaurantQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@ { request ->
    val idString = request.path("restaurant").orEmpty()
    val id = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQueries.FetchRestaurantViaId().invoke(id) ?: return@handler Response(Status.BAD_REQUEST)
    val permissions = permissionLens(request)
    if (!permissions.editRestaurant)
        Response (Status.UNAUTHORIZED)
    val webForm = BodyRestaurantFormLens(request)
    if (webForm.errors.isEmpty()) {
        restaurantQueries.EditRestaurantQuery().invoke(restaurantNameFormLens(webForm), restaurant)
        Response(Status.FOUND).header("Location", "/restaurants")
    } else {
        Response(Status.OK).with(htmlView(request) of ShowEditRestaurantFormVM(webForm))
    }
}
fun showEditRestaurantForm(
    permissionLens: RequestContextLens<RolePermissions>,
    htmlView: ContextAwareViewRender,
): HttpHandler = { request ->
    val permissions = permissionLens(request)
    if (!permissions.editRestaurant)
        Response(Status.UNAUTHORIZED)
    Response(Status.OK).with(htmlView(request) of ShowEditRestaurantFormVM())
}