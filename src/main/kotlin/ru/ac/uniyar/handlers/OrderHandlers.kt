package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.*
import ru.ac.uniyar.models.*
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.DishQueries
import ru.ac.uniyar.queries.OrderQueries
import ru.ac.uniyar.queries.RestaurantQueries
import ru.ac.uniyar.queries.UserQueries

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
        if (!permissions.viewBasket || user==null) {
            return Response(Status.UNAUTHORIZED)
        }
        return Response(Status.OK).with(
            htmlView(request) of BasketVM(orderQueries.OrdersForUserQ().invoke(user.id))
        )
    }
}

class OrdersH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender
):HttpHandler{
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.listOrders || user == null) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.OrdersForOperatorQ().invoke(user.restaurantId)
        return Response(Status.OK).with(
            htmlView(request) of OrdersVM(order)
        )
    }
}


class AddDishToOrderH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val dishQueries: DishQueries,
    private val restaurantQueries: RestaurantQueries,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.createOrder || user == null) {
            return Response(Status.UNAUTHORIZED)
        }
        val dish = dishQueries.FetchDishQ().invoke(UUID.fromString(request.path("dish").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(UUID.fromString(request.path("restaurant").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val order = if (!orderQueries.CheckOrderQ().invoke(user.id)) {
            orderQueries.CreateOrderQ().invoke(user.id, dish)
        } else {
            orderQueries.AddDishQ().invoke(user.id, dish)
        }
        orderQueries.UpdateOrderQ().invoke(order)
        return Response(Status.FOUND).header(
            "Location", "/${restaurant.id}/ListOfDishes"
        )
    }
}

class OrderH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.viewOrder|| user == null) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(UUID.fromString(request.path("order").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(
            htmlView(request) of OrderInBasketVM(order)
        )
    }
}

class OrderForOperatorH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val orderQueries: OrderQueries,
    private val userQueries: UserQueries,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler{
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.viewOrder) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(UUID.fromString(request.path("order").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val user = userQueries.FetchUserQ().invoke(order.clientId)
            ?: return Response(Status.BAD_REQUEST)
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(order.restaurantId)
            ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(
            htmlView(request) of OrderVM(order, user, restaurant)
        )
    }
}

class DeleteOrderH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.deleteOrder || !permissions.viewBasket || user == null) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(UUID.fromString(request.path("order").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        orderQueries.DeleteOrderQ().invoke(order.id)
        return Response(Status.OK).with(
            htmlView(request) of BasketVM(orderQueries.OrdersForUserQ().invoke(user.id))
        )
    }
}

class DeleteDishFromOrderH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val dishQueries: DishQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.viewOrder|| user==null ) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(UUID.fromString(request.path("order").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val dish = dishQueries.FetchDishQ().invoke(UUID.fromString(request.path("dish").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val newOrder = orderQueries.DeleteDishQ().invoke(order, dish.id)
        orderQueries.UpdateOrderQ().invoke(newOrder)
        return Response(Status.OK).with(
            htmlView(request) of OrderInBasketVM(newOrder)
        )
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
        if (!permissions.viewBasket || user == null) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(UUID.fromString(request.form().findSingle("orderId").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        orderQueries.EditStatusQ().invoke(order, "В обработке")
        return Response(Status.OK).with(
            htmlView(request) of BasketVM(orderQueries.OrdersForUserQ().invoke(user.id))
        )
    }
}

class EditStatusByOperatorH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val orderQueries: OrderQueries,
):HttpHandler{
    companion object {
        val statusFormLens = FormField.string().required("status")
        val BodyFormLens = Body.webForm(
            Validator.Feedback, statusFormLens,
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.editOrder) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(UUID.fromString(request.path("order").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val webForm = BodyFormLens(request)
        orderQueries.EditStatusQ().invoke(order, statusFormLens(webForm))
        return Response(Status.FOUND).header(
            "Location", "/orders/${order.id}"
        )
    }
}