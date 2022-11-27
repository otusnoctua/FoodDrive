package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.Order
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.domain.User
import ru.ac.uniyar.models.ShowBasketVM
import ru.ac.uniyar.models.ShowListOfDishesVM
import ru.ac.uniyar.models.ShowOrderVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.DishQueries
import ru.ac.uniyar.queries.OrderQueries
import ru.ac.uniyar.queries.RestaurantQueries

import java.time.LocalDateTime
import java.util.*

class ShowBasket(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val currentUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.listOrders)
            return Response(Status.UNAUTHORIZED)
        val userId= currentUserLens(request)!!.id
        return Response(Status.OK).with(htmlView(request) of ShowBasketVM(orderQueries.FetchOrdersViaUserId().invoke(userId)))
    }
}


class AddDishToOrder(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val currentUserLens: RequestContextLens<User?>,
    private val htmlView: ContextAwareViewRender,
    private val orderQueries: OrderQueries,
    private val dishQueries: DishQueries,
    private val restaurantQueries: RestaurantQueries,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.createOrder)
            return Response(Status.UNAUTHORIZED)
        val userId = currentUserLens(request)!!.id
        val params = request.form()
        val idString = params.findSingle("id").orEmpty()
        val id = UUID.fromString(idString)
        val restaurantId = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)

        if (!orderQueries.CheckOrder().invoke(userId)) {
            val dishes = mutableListOf<String>()
            dishes.add(dishQueries.FetchNameDishViaId().invoke(id))
            val order = Order(
                EMPTY_UUID,
                userId,
                restaurantId,
                "В ожидании",
                LocalDateTime.now(),
                dishes
            )
            orderQueries.AddOrder().invoke(order)
        } else {
            orderQueries.UpdateOrder().invoke(orderQueries.AddDish().invoke(userId, id))
        }
        val model =
            restaurantQueries.FetchRestaurantViaId().invoke(restaurantId)
                ?.let { ShowListOfDishesVM(dishQueries.ListOfDishes().invoke(restaurantId), it) }
        return Response(Status.OK).with(htmlView(request) of model!!)
    }
}

class ShowOrder(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.viewOrder)
            return Response(Status.UNAUTHORIZED)
        val idString = request.path("order").orEmpty()
        val id = UUID.fromString(idString) ?: return Response(Status.BAD_REQUEST)
        val order = orderQueries.FetchOrderViaId().invoke(id) ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(htmlView(request) of ShowOrderVM(order))
    }
}

class DeleteOrder(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val currentUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val idString = request.path("order").orEmpty()
        val id = UUID.fromString(idString) ?: return Response(Status.BAD_REQUEST)
        val permissionsDelete = permissionsLens(request)
        if (!permissionsDelete.deleteOrder)
            return Response(Status.UNAUTHORIZED)
        val permissions = permissionsLens(request)
        if (!permissions.listOrders)
            return Response(Status.UNAUTHORIZED)
        orderQueries.DeleteOrder().invoke(id)
        val userId = currentUserLens(request)!!.id
        return Response(Status.OK).with(htmlView(request) of ShowBasketVM(orderQueries.FetchOrdersViaUserId().invoke(userId)))
    }
}
class DeleteDishFromOrder(
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
        val order = orderQueries.FetchOrderViaId().invoke(id) ?: return Response(Status.BAD_REQUEST)
        val indexString = request.path("dish").orEmpty()
        val index = indexString.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        val newOrder = orderQueries.DeleteDish().invoke(order, index)
        orderQueries.UpdateOrder().invoke(newOrder)
        return Response(Status.OK).with(htmlView(request) of ShowOrderVM(newOrder))
    }
}

class EditStatusByUser(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val currentUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.listOrders)
            return Response(Status.UNAUTHORIZED)
        val userId = currentUserLens(request)!!.id
        val idString = request.form().findSingle("index").orEmpty()
        val index = idString.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        orderQueries.EditStatus().invoke(index, "В обработке", userId)
        return Response(Status.OK).with(htmlView(request) of ShowBasketVM(orderQueries.FetchOrdersViaUserId().invoke(userId)))
    }
}