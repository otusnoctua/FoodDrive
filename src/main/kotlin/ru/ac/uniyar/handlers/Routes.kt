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

class ShowListOfRestaurants(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val dishQueries: DishQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.listRestaurants)
            return Response(Status.UNAUTHORIZED)
        val model = ShowListOfRestaurantsVM(
            restaurantQueries.ListOfRestaurantsQuery().invoke().map {
                RestaurantInfo(
                    it,
                    dishQueries.ListOfDishes().invoke(it.id).map { it.restaurantId }.contains(it.id)
                )
            })
        return Response(Status.OK).with(htmlView(request) of model)
    }
}
class ShowListOfDishes(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val dishQueries: DishQueries,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.listDishes)
            return Response(Status.UNAUTHORIZED)
        val id = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val restaurant =
            restaurantQueries.FetchRestaurantViaId().invoke(id) ?: return Response(Status.BAD_REQUEST)
        val model = ShowListOfDishesVM(dishQueries.ListOfDishes().invoke(restaurant.id), restaurant)
        return Response(Status.OK).with(htmlView(request) of model)
    }
}