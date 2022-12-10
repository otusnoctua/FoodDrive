package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.format.JsonType
import org.http4k.lens.*
import ru.ac.uniyar.domain.RestaurantInfo
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.domain.lensOrNull
import ru.ac.uniyar.models.RestaurantsVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.DishQueries
import ru.ac.uniyar.queries.RestaurantQueries
import ru.ac.uniyar.queries.ReviewQueries


class PingHandler(): HttpHandler {
    override fun invoke(request: Request): Response {
        return Response(Status.OK).body("pong")
    }
}
class RedirectToHomePage(): HttpHandler {
    override fun invoke(request: Request): Response {
        return Response(Status.FOUND).header(
            "Location", "/restaurants"
        )
    }
}

class HomePageH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val reviewQueries: ReviewQueries,
    private val dishQueries: DishQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    companion object {
        val restaurantNameLens= Query.string().optional("name")
        val flagLens=Query.int().optional("rating")

    }
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.listRestaurants) {
            return Response(Status.UNAUTHORIZED)
        }
        val restaurantName= lensOrNull(restaurantNameLens,request)
        val flag = lensOrNull(flagLens,request)
        val model = RestaurantsVM(
            restaurantQueries.FilterByNameQ().invoke(restaurantName,flag).map {
                RestaurantInfo(
                    it,
                    dishQueries.DishesOfRestaurantQ().invoke(it.id).isNotEmpty(),
                    reviewQueries.RatingForRestaurantQ().invoke(it.id)
                )
            },
        name = restaurantName)
        return Response(Status.OK).with(
            htmlView(request) of model)

    }
}
