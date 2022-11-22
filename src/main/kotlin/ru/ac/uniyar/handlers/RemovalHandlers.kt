package ru.ac.uniyar.handlers

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.RequestContextLens
import org.http4k.routing.path
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.queries.*
import java.util.*

fun deleteRestaurant(
    permissionLens: RequestContextLens<RolePermissions>,
    restaurantQueries: RestaurantQueries,
    dishQueries: DishQueries,
): HttpHandler = handler@ { request ->
    val idString = request.path("restaurant").orEmpty()
    val restaurantId = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQueries.FetchRestaurantViaId().invoke(restaurantId) ?: return@handler Response(Status.BAD_REQUEST)
    val permissionsDelete = permissionLens(request)
    val haveDishes = dishQueries.ListOfDishes().invoke(restaurant.id).map {it.restaurantId}.contains(restaurant.id)
    if (haveDishes)
        Response(Status.BAD_REQUEST)
    if (!permissionsDelete.deleteRestaurant)
        Response (Status.UNAUTHORIZED)
    val permissions = permissionLens(request)
    if (!permissions.listRestaurants)
        Response(Status.UNAUTHORIZED)
    restaurantQueries.DeleteRestaurantQuery().invoke(restaurantId)
    Response(Status.FOUND).header("Location", "/restaurants")
}

fun deleteDish(
    permissionLens: RequestContextLens<RolePermissions>,
   dishQueries: DishQueries,
): HttpHandler = handler@ { request ->
    val idString = request.path("dish").orEmpty()
    val dishId = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val dish = dishQueries.FetchDishViaId().invoke(dishId) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurantIdString = request.path("restaurant").orEmpty()
    val permissionsDelete = permissionLens(request)
    if (!permissionsDelete.deleteDish)
        Response (Status.UNAUTHORIZED)
    val permissions = permissionLens(request)
    if (!permissions.listDishes)
        Response(Status.UNAUTHORIZED)
    dishQueries.DeleteDishQuery().invoke(dish)
    Response(Status.FOUND).header("Location", "/"+restaurantIdString+"/ListOfDishes")
}