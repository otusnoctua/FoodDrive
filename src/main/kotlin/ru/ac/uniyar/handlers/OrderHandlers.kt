package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.Order
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.domain.User
import ru.ac.uniyar.models.BasketVM
import ru.ac.uniyar.models.RestaurantVM
import ru.ac.uniyar.models.OrderVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.DishQueries
import ru.ac.uniyar.queries.OrderQueries
import ru.ac.uniyar.queries.RestaurantQueries

import java.time.LocalDateTime
import java.util.*

class BasketH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.listOrders || user==null)
            return Response(Status.UNAUTHORIZED)
        return Response(Status.OK).with(htmlView(request) of BasketVM(orderQueries.OrdersByUserQ().invoke(user.id)))
    }
}


class AddDishToOrderH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val htmlView: ContextAwareViewRender,
    private val orderQueries: OrderQueries,
    private val dishQueries: DishQueries,
    private val restaurantQueries: RestaurantQueries,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.createOrder || user == null)
            return Response(Status.UNAUTHORIZED)
        val params = request.form()
        val dishId = UUID.fromString(params.findSingle("id").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val dish = dishQueries.FetchDishQ().invoke(dishId) ?: return Response(Status.BAD_REQUEST)
        val restaurantId = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(restaurantId) ?: return Response(Status.BAD_REQUEST)

        if (!orderQueries.CheckOrderQ().invoke(user.id)) {
            val dishes = mutableListOf<String>()
            dishes.add(dish.nameDish)
            val order = Order(
                EMPTY_UUID,
                user.id,
                restaurantId,
                "В ожидании",
                LocalDateTime.now(),
                dishes
            )
            orderQueries.AddOrderQ().invoke(order)
        } else {
            orderQueries.UpdateOrderQ().invoke(orderQueries.AddDishQ().invoke(user.id, dishId))
        }
        val model = RestaurantVM(dishQueries.DishesOfRestaurantQ().invoke(restaurant.id), restaurant)
        return Response(Status.OK).with(htmlView(request) of model)
    }
}

class OrderH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.viewOrder)
            return Response(Status.UNAUTHORIZED)
        val orderId = UUID.fromString(request.path("order").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val order = orderQueries.FetchOrderQ().invoke(orderId) ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(htmlView(request) of OrderVM(order))
    }
}

class DeleteOrderH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val id = UUID.fromString(request.path("order").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val permissionsDelete = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissionsDelete.deleteOrder || user == null)
            return Response(Status.UNAUTHORIZED)
        val permissions = permissionsLens(request)
        if (!permissions.listOrders)
            return Response(Status.UNAUTHORIZED)
        orderQueries.DeleteOrderQ().invoke(id)
        return Response(Status.OK).with(htmlView(request) of BasketVM(orderQueries.OrdersByUserQ().invoke(user.id)))
    }
}
class DeleteDishFromOrderH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.viewOrder)
            return Response(Status.UNAUTHORIZED)
        val idString = request.path("order").orEmpty()
        val id = UUID.fromString(idString) ?: return Response(Status.BAD_REQUEST)
        val order = orderQueries.FetchOrderQ().invoke(id) ?: return Response(Status.BAD_REQUEST)
        val indexString = request.path("dish").orEmpty()
        val index = indexString.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        val newOrder = orderQueries.DeleteDishQ().invoke(order, index)
        orderQueries.UpdateOrderQ().invoke(newOrder)
        return Response(Status.OK).with(htmlView(request) of OrderVM(newOrder))
    }
}

class EditStatusByUserH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.listOrders || user == null)
            return Response(Status.UNAUTHORIZED)
        val index = request.form().findSingle("index").orEmpty().toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        orderQueries.EditStatusQ().invoke(index, "В обработке", user.id)
        return Response(Status.OK).with(htmlView(request) of BasketVM(orderQueries.OrdersByUserQ().invoke(user.id)))
    }
}