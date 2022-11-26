package ru.ac.uniyar.handlers

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.RequestContextLens
import org.http4k.routing.path
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.queries.*
import java.util.*

class DeleteRestaurant(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val dishQueries: DishQueries,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val restaurantId = request.path("restaurant")!!.toInt()
        val restaurant =
            restaurantQueries.FetchRestaurantViaId().invoke(restaurantId) ?: return Response(Status.BAD_REQUEST)
        val permissionsDelete = permissionLens(request)
        val haveDishes =
            dishQueries.ListOfDishes().invoke(restaurant.id).map { it.restaurantId }.contains(restaurant.id)
        if (haveDishes)
            return Response(Status.BAD_REQUEST)
        if (!permissionsDelete.deleteRestaurant)
            return Response(Status.UNAUTHORIZED)
        val permissions = permissionLens(request)
        if (!permissions.listRestaurants)
            return Response(Status.UNAUTHORIZED)
        restaurantQueries.DeleteRestaurantQuery().invoke(restaurantId)
        return Response(Status.FOUND).header("Location", "/restaurants")
    }
}

class DeleteDish(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val dishQueries: DishQueries,
): HttpHandler {
    override fun invoke(request: Request): Response {

        val dishId = request.path("dish")!!.toInt()
        val dish = dishQueries.FetchDishViaId().invoke(dishId) ?: return Response(Status.BAD_REQUEST)
        val restaurantId = request.path("restaurant")!!.toInt()

        val permissionsDelete = permissionLens(request)
        if (!permissionsDelete.deleteDish)
            return Response(Status.UNAUTHORIZED)

        val permissions = permissionLens(request)
        if (!permissions.listDishes)
            return Response(Status.UNAUTHORIZED)

        dishQueries.DeleteDishQuery().invoke(dish)
        return Response(Status.FOUND).header("Location", "/${restaurantId}/ListOfDishes")

    }

}