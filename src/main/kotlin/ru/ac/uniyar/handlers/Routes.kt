package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.routing.path
import ru.ac.uniyar.models.ShowListOfDishesVM
import ru.ac.uniyar.models.ShowListOfRestaurantsVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.ListOfDishesQuery
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
    listOfRestaurantsQuery: ListOfRestaurantsQuery,
    htmlView: ContextAwareViewRender,
): HttpHandler = {request ->
    val model = ShowListOfRestaurantsVM(listOfRestaurantsQuery.invoke())
    Response(Status.OK).with(htmlView(request) of model)
}
fun showListOfDishes(
    listOfDishesQuery: ListOfDishesQuery,
    restaurantQuery: RestaurantQuery,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@ {request ->
    val restaurantIdString = request.path("restaurants").orEmpty()
    val id = UUID.fromString(restaurantIdString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQuery.invoke(id) ?: return@handler Response(Status.BAD_REQUEST)
    val model = ShowListOfDishesVM(listOfDishesQuery.invoke(restaurant.id), restaurant)
    Response(Status.OK).with(htmlView(request) of model)
}