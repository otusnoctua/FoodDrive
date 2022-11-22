package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.RequestContextLens
import org.http4k.routing.path
import ru.ac.uniyar.domain.RestaurantInfo
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.ShowListOfDishesVM
import ru.ac.uniyar.models.ShowListOfRestaurantsVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.DishQueries
import ru.ac.uniyar.queries.RestaurantQueries
import java.util.*

class PingHandler(): HttpHandler {
    override fun invoke(request: Request): Response {
        return Response(Status.OK).body("pong")
    }
}
class RedirectToRestaurants(): HttpHandler {
    override fun invoke(request: Request): Response {
        return Response(Status.FOUND).header("Location", "/restaurants")
    }
}

fun showListOfRestaurants(
    permissionsLens: RequestContextLens<RolePermissions>,
    restaurantQueries: RestaurantQueries,
    dishQueries: DishQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = {request ->
    val permissions = permissionsLens(request)
    if (!permissions.listRestaurants)
        Response(Status.UNAUTHORIZED)
    val model = ShowListOfRestaurantsVM(restaurantQueries.ListOfRestaurantsQuery().invoke().map{RestaurantInfo(it, dishQueries.ListOfDishes().invoke(it.id).map {it.restaurantId}.contains(it.id))})
    Response(Status.OK).with(htmlView(request) of model)
}
fun showListOfDishes(
    permissionsLens: RequestContextLens<RolePermissions>,
   dishQueries: DishQueries,
    restaurantQueries: RestaurantQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@ {request ->
    val permissions = permissionsLens(request)
    if (!permissions.listDishes)
        Response(Status.UNAUTHORIZED)
    val restaurantIdString = request.path("restaurant").orEmpty()
    val id = UUID.fromString(restaurantIdString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQueries.FetchRestaurantViaId().invoke(id) ?: return@handler Response(Status.BAD_REQUEST)
    val model = ShowListOfDishesVM(dishQueries.ListOfDishes().invoke(restaurant.id), restaurant)
    Response(Status.OK).with(htmlView(request) of model)
}