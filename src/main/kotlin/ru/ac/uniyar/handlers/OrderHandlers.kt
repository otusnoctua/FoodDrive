package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.lens.*
import org.http4k.routing.path
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

class ShowBasket(
    private val curUserLens: RequestContextLens<User?>,
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val curUser = curUserLens(request)
        val permissions = permissionsLens(request)
        if (!permissions.listOrders || curUser == null)
            return Response(Status.UNAUTHORIZED)
        val userId = curUser.id
        return Response(Status.OK).with(htmlView(request) of ShowBasketVM(orderQueries.FetchOrdersViaUserId().invoke(userId)))
    }
}

class AddDishToOrder(
    private val curUserLens: RequestContextLens<User?>,
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val htmlView: ContextAwareViewRender,
    private val orderQueries: OrderQueries,
    private val dishQueries: DishQueries,
    private val restaurantQueries: RestaurantQueries,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val curUser = curUserLens(request)
        val permissions = permissionsLens(request)
        if (!permissions.createOrder || curUser == null)
            return Response(Status.UNAUTHORIZED)
        val userId = curUser.id
        val dishId = request.form().findSingle("id").orEmpty().toInt()
        val restaurantId = request.path("restaurant")?.toInt() ?: return Response(Status.BAD_REQUEST)

        if (!orderQueries.CheckOrder().invoke(userId)) {
            val order = Order(
                0,
                userId,
                restaurantId,
                "В ожидании",
                LocalDateTime.now(),
                mutableListOf<Int>()
            )
            orderQueries.AddOrder().invoke(order)
        }
        orderQueries.AddDish().invoke(userId, dishId)

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
        val id = request.path("order")!!.toInt()
        val order = orderQueries.FetchOrderViaId().invoke(id) ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(htmlView(request) of ShowOrderVM(order))
    }
}

class DeleteOrder(
    private val curUserLens: RequestContextLens<User?>,
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val curUser = curUserLens(request)
        val id = request.path("order")?.toInt() ?: return Response(Status.BAD_REQUEST)

        val permissionsDelete = permissionsLens(request)
        if (curUser == null){
            return Response(Status.UNAUTHORIZED)
        }
        if (!permissionsDelete.deleteOrder)
            return Response(Status.UNAUTHORIZED)
        val permissions = permissionsLens(request)
        if (!permissions.listOrders)
            return Response(Status.UNAUTHORIZED)
        orderQueries.DeleteOrder().invoke(id)
        val userId = curUser.id
        return Response(Status.OK).with(htmlView(request) of ShowBasketVM(orderQueries.FetchOrdersViaUserId().invoke(userId)))
    }
}

class DeleteDishFromOrder(
    private val curUserLens: RequestContextLens<User?>,
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val curUser = curUserLens(request)
        val permissions = permissionsLens(request)
        if (!permissions.viewOrder || curUser == null)
            return Response(Status.UNAUTHORIZED)
        val orderId = request.path("order")?.toInt() ?: return Response(Status.BAD_REQUEST)
        val order = orderQueries.FetchOrderViaId().invoke(orderId) ?: return Response(Status.BAD_REQUEST)
        val dishId = request.path("dish")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        orderQueries.DeleteDish().invoke(order, dishId) ?: return Response(Status.BAD_REQUEST)
        val newOrder = orderQueries.FetchOrderViaId().invoke(orderId) ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(htmlView(request) of ShowOrderVM(newOrder))
    }
}

class EditStatusByUser(
    private val curUserLens: RequestContextLens<User?>,
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    override fun invoke(request: Request): Response {
        val curUser = curUserLens(request)
        val permissions = permissionsLens(request)
        if (!permissions.listOrders || curUser == null)
            return Response(Status.UNAUTHORIZED)
        val userId = curUser.id
        val idString = request.form().findSingle("index").orEmpty()
        val index = idString.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        orderQueries.EditStatus().invoke(index, "В обработке", userId)
        return Response(Status.OK).with(htmlView(request) of ShowBasketVM(orderQueries.FetchOrdersViaUserId().invoke(userId)))
    }
}