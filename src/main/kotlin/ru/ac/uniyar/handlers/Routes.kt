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
import ru.ac.uniyar.queries.ListOfRestaurantsQuery
import ru.ac.uniyar.queries.RestaurantQuery
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
    listOfRestaurantsQuery: ListOfRestaurantsQuery,
    dishQueries: DishQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = {request ->
    val permissions = permissionsLens(request)
    if (!permissions.listRestaurants)
        Response(Status.UNAUTHORIZED)
    val model = ShowListOfRestaurantsVM(listOfRestaurantsQuery.invoke().map{RestaurantInfo(it, dishQueries.listOfDishes(it.id).map {it.restaurant_id}.contains(it.id))})
    Response(Status.OK).with(htmlView(request) of model)
}
fun showListOfDishes(
    permissionsLens: RequestContextLens<RolePermissions>,
   dishQueries: DishQueries,
    restaurantQuery: RestaurantQuery,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@ {request ->
    val permissions = permissionsLens(request)
    if (!permissions.listDishes)
        Response(Status.UNAUTHORIZED)
    val restaurantIdString = request.path("restaurant").orEmpty()
    val id = UUID.fromString(restaurantIdString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQuery.invoke(id) ?: return@handler Response(Status.BAD_REQUEST)
    val model = ShowListOfDishesVM(dishQueries.listOfDishes(restaurant.id), restaurant)
    Response(Status.OK).with(htmlView(request) of model)
}