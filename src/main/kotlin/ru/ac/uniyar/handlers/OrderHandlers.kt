package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.Order
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.domain.User
import ru.ac.uniyar.models.*
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.DishQueries
import ru.ac.uniyar.queries.OrderQueries
import ru.ac.uniyar.queries.RestaurantQueries
import ru.ac.uniyar.queries.UserQueries

import java.util.*

class OrdersH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.viewBasket || user == null) {
            return Response(Status.UNAUTHORIZED)
        }
        return Response(Status.OK).with(
            htmlView(request) of OrdersVM(
                orderQueries.AcceptedOrdersQ().invoke(user.id).filter { it.status == "В обработке" },
                orderQueries.AcceptedOrdersQ().invoke(user.id).filter { it.status == "Готов" },
            )
        )
    }
}

class OperatorOrdersH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.listOrders || user == null) {
            return Response(Status.UNAUTHORIZED)
        }
        return Response(Status.OK).with(
            htmlView(request) of OperatorOrdersVM(
                orderQueries.OrdersForOperatorQ().invoke(user.restaurantId)
            )
        )
    }
}

class BasketH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.viewBasket || user == null) {
            return Response(Status.UNAUTHORIZED)
        }
        return Response(Status.OK).with(
            htmlView(request) of BasketVM(orderQueries.WaitingOrdersQ().invoke(user.id))
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

class OrderFromBasketH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val dishQueries: DishQueries,
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
        val dishes = order.dishes.map { dishQueries.FetchDishQ().invoke(it) }
        if (dishes.contains(null)) {
            return Response(Status.BAD_REQUEST)
        }
        var price = 29
        dishes.forEach { price += it!!.price }

        return Response(Status.OK).with(
            htmlView(request) of UserOrderVM(order, dishes.map { it!! }, price)
        )
    }
}

class OrderForOperatorH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val orderQueries: OrderQueries,
    private val dishQueries: DishQueries,
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
        val dishes = order.dishes.map { dishQueries.FetchDishQ().invoke(it) }
        if (dishes.contains(null)) {
            return Response(Status.BAD_REQUEST)
        }
        var price = 29
        val user = userQueries.FetchUserViaId().invoke(order.clientId)?: return Response(Status.BAD_REQUEST)
        val restaurant=restaurantQueries.FetchRestaurantQ().invoke(order.restaurantId)?:return Response(Status.BAD_REQUEST)
        dishes.forEach { price += it!!.price }
        return Response(Status.OK).with(
            htmlView(request) of OperatorOrderVM(order, dishes.map { it!! }, price,restaurant,user)
        )
    }
}

class OrderH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val orderQueries: OrderQueries,
    private val dishQueries: DishQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.viewOrder) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(UUID.fromString(request.path("order").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val dishes = order.dishes.map { dishQueries.FetchDishQ().invoke(it) }
        if (dishes.contains(null)) {
            return Response(Status.BAD_REQUEST)
        }
        var price = 29
        dishes.forEach { price += it!!.price }
        return Response(Status.OK).with(
            htmlView(request) of UserOrderVM(order, dishes.map { it!! }, price)
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
            htmlView(request) of BasketVM(orderQueries.WaitingOrdersQ().invoke(user.id))
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
        if (!permissions.viewOrder|| user == null ) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(UUID.fromString(request.path("order").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val dish = dishQueries.FetchDishQ().invoke(UUID.fromString(request.path("dish").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val newOrder = orderQueries.DeleteDishQ().invoke(order, dish.id)
        orderQueries.UpdateOrderQ().invoke(newOrder)
        val dishes = newOrder.dishes.map { dishQueries.FetchDishQ().invoke(it) }
        if (dishes.contains(null)) {
            return Response(Status.BAD_REQUEST)
        }
        var price = 29
        dishes.forEach { price += it!!.price }
        return Response(Status.OK).with(
            htmlView(request) of UserOrderVM(newOrder, dishes.map { it!! }, price)
        )
    }
}

class EditStatusByUserH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val dishQueries: DishQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.viewBasket || user == null) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(UUID.fromString(request.path("order").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val dishes = order.dishes.map { dishQueries.FetchDishQ().invoke(it) }
        if (dishes.contains(null)) {
            return Response(Status.BAD_REQUEST)
        }
        var price = 29
        dishes.forEach { price += it!!.price }
        orderQueries.EditStatusQ().invoke(
            order = orderQueries.SetPriceQ().invoke(order, price),
            string = "В обработке",
        )
        return Response(Status.OK).with(
            htmlView(request) of BasketVM(orderQueries.WaitingOrdersQ().invoke(user.id))
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