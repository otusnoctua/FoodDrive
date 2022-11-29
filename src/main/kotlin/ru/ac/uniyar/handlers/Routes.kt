package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.RequestContextLens
import ru.ac.uniyar.domain.RestaurantInfo
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.RestaurantsVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.DishQueries
import ru.ac.uniyar.queries.RestaurantQueries


class PingHandler(): HttpHandler {
    override fun invoke(request: Request): Response {
        return Response(Status.OK).body("pong")
    }
}
class RedirectToHomePage(): HttpHandler {
    override fun invoke(request: Request): Response {
        return Response(Status.FOUND).header("Location", "/restaurants")
    }
}

class HomePageH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val dishQueries: DishQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.listRestaurants)
            return Response(Status.UNAUTHORIZED)
        val model = RestaurantsVM(
            restaurantQueries.RestaurantsQ().invoke().map {
                RestaurantInfo(
                    it,
                    dishQueries.DishesOfRestaurantQ().invoke(it.id).isNotEmpty()
                )
            })
        return Response(Status.OK).with(htmlView(request) of model)
    }
}
