package ru.ac.uniyar.handlers

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import org.http4k.routing.path
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.ShowListOfRestaurantsVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.DeleteRestaurantQuery
import ru.ac.uniyar.queries.ListOfDishesQuery
import ru.ac.uniyar.queries.ListOfRestaurantsQuery
import ru.ac.uniyar.queries.RestaurantQuery
import java.util.*

fun deleteRestaurant(
    permissionLens: RequestContextLens<RolePermissions>,
    restaurantQuery: RestaurantQuery,
    deleteRestaurantQuery: DeleteRestaurantQuery,
    listOfDishesQuery: ListOfDishesQuery,
): HttpHandler = handler@ { request ->
    val idString = request.path("restaurant").orEmpty()
    val restaurantId = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQuery.invoke(restaurantId) ?: return@handler Response(Status.BAD_REQUEST)
    val permissionsDelete = permissionLens(request)
    val haveDishes = listOfDishesQuery.invoke(restaurant.id).map {it.restaurant_id}.contains(restaurant.id)
    if (haveDishes)
        Response(Status.BAD_REQUEST)
    if (!permissionsDelete.deleteRestaurant)
        Response (Status.UNAUTHORIZED)
    val permissions = permissionLens(request)
    if (!permissions.listRestaurants)
        Response(Status.UNAUTHORIZED)
    deleteRestaurantQuery.invoke(restaurantId)
    Response(Status.FOUND).header("Location", "/restaurants")
}