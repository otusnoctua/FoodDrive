package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.Order
import ru.ac.uniyar.domain.OrderInfo
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
                orderQueries.AcceptedOrdersQ().invoke(user.id).filter { it.orderStatus == "В обработке" },
                orderQueries.AcceptedOrdersQ().invoke(user.id).filter { it.orderStatus == "Готов" },
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
        if (!permissions.listOrders || user == null || user.restaurant == null) {
            return Response(Status.UNAUTHORIZED)
        }
        return Response(Status.OK).with(
            htmlView(request) of OperatorOrdersVM(
                orderQueries.OrdersForOperatorQ().invoke(user.restaurant!!.id)
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
            htmlView(request) of BasketVM(orderQueries.WaitingOrdersQ().invoke(user.id).map {
                OrderInfo(
                    it,
                    it.dishes.sumOf { dish -> dish.price }+29
                ) })
        )
    }
}


class AddDishToOrderH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
    private val dishQueries: DishQueries,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.createOrder || user == null) {
            return Response(Status.UNAUTHORIZED)
        }
        val dish = dishQueries.FetchDishQ().invoke(
            request.path("dish")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val restaurant = dish.restaurant
        val order = if (!orderQueries.CheckOrderQ().invoke(user.id,restaurant.id)) {
            orderQueries.CreateOrderQ().invoke(user, dish)
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
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.viewOrder|| user == null) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(
            request.path("order")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val restaurant = order.restaurant
        val dishes = order.dishes
        val price = dishes.sumOf { it.price } + 29
        return Response(Status.OK).with(
            htmlView(request) of UserOrderVM(order, dishes.map { it }, price, restaurant)
        )
    }
}

class OrderForOperatorH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
):HttpHandler{
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.viewOrder) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(
            request.path("order")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        )
            ?: return Response(Status.BAD_REQUEST)

        val user = order.client
        val restaurant = order.restaurant
        val orderCheck = orderQueries.FetchOrderCheck().invoke(order)
        val orderDishes = orderQueries.FetchOrderDishes().invoke(order)
        return Response(Status.OK).with(
            htmlView(request) of OperatorOrderVM(
                order,
                orderDishes,
                orderCheck,
                restaurant,
                user)
        )
    }
}

class OrderH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val orderQueries: OrderQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.viewOrder) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(
            request.path("order")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val restaurant = order.restaurant
        val dishes = order.dishes
        val price = dishes.sumOf { it.price } + 29
        return Response(Status.OK).with(
            htmlView(request) of UserOrderVM(order, dishes.map { it }, price, restaurant)
        )
    }
}

class DeleteOrderH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val orderQueries: OrderQueries,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        val user = curUserLens(request)
        if (!permissions.deleteOrder || !permissions.viewBasket || user == null) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(
            request.path("order")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        orderQueries.DeleteOrderQ().invoke(order)
        return Response(Status.FOUND).header(
            "Location", "/"
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
        val order = orderQueries.FetchOrderQ().invoke(
            request.path("order")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val restaurant = order.restaurant
        val dish = dishQueries.FetchDishQ().invoke(
            request.path("dish")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val newOrder = orderQueries.DeleteDishQ().invoke(order, dish.id)
        orderQueries.UpdateOrderQ().invoke(newOrder)
        val dishes = newOrder.dishes
        val price = dishes.sumOf { it.price } + 29
        return Response(Status.OK).with(
            htmlView(request) of UserOrderVM(newOrder, dishes.map { it }, price, restaurant)
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
        val order = orderQueries.FetchOrderQ().invoke(
            request.path("order")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val dishes = order.dishes
        val price = dishes.sumOf { it.price } + 29
        orderQueries.EditStatusQ().invoke(
            order = orderQueries.SetPriceQ().invoke(order, price),
            string = "В обработке",
        )
        return Response(Status.OK).with(
            htmlView(request) of BasketVM(
                orderQueries.WaitingOrdersQ().invoke(user.id).map {
                    OrderInfo(
                        it,
                        it.dishes.sumOf { dish -> dish.price }+29
                    ) }
            )
        )
    }
}

class EditStatusByOperatorH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
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
        val user = curUserLens(request)
        if (!permissions.editOrder) {
            return Response(Status.UNAUTHORIZED)
        }
        val order = orderQueries.FetchOrderQ().invoke(
            request.path("order")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        if(user == null || user.restaurant != order.restaurant){
            return Response(Status.UNAUTHORIZED)
        }
        val webForm = BodyFormLens(request)
        orderQueries.SetEndTimeQ().invoke(
            order = orderQueries.EditStatusQ().invoke(order, statusFormLens(webForm))
        )
        return Response(Status.FOUND).header(
            "Location", "/orders"
        )
    }
}