package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.Order
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.ShowBasketVM
import ru.ac.uniyar.models.ShowListOfDishesVM
import ru.ac.uniyar.models.ShowOrderVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.DishQueries
import ru.ac.uniyar.queries.OrderQueries
import ru.ac.uniyar.queries.RestaurantQueries

import java.time.LocalDateTime
import java.util.*

fun showBasket(
    permissionsLens: RequestContextLens<RolePermissions>,
    orderQueries: OrderQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = { request ->
    val permissions = permissionsLens(request)
    if (!permissions.listOrders)
        Response(Status.UNAUTHORIZED)
    val userId = permissions.id

    Response(Status.OK).with(htmlView(request) of ShowBasketVM(orderQueries.FetchOrdersViaUserId().invoke(userId)))

}


fun addDishToOrder(
    permissionsLens: RequestContextLens<RolePermissions>,
    htmlView: ContextAwareViewRender,
    orderQueries: OrderQueries,
    dishQueries: DishQueries,
    restaurantQueries: RestaurantQueries,
): HttpHandler = handler@{ request ->
    val permissions = permissionsLens(request)
    if (!permissions.createOrder)
        Response(Status.UNAUTHORIZED)
    val userId = permissions.id
    val params = request.form()
   val idString = params.findSingle("id").orEmpty()
    val id = UUID.fromString(idString)
    val restaurantIdString = request.path("restaurant").orEmpty()
    val restaurantId = UUID.fromString(restaurantIdString) ?: return@handler Response(Status.BAD_REQUEST)

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
        restaurantQueries.FetchRestaurantViaId().invoke(restaurantId)?.let { ShowListOfDishesVM(dishQueries.ListOfDishes().invoke(restaurantId), it) }
    Response(Status.OK).with(htmlView(request) of model!!)

}

fun showOrder(
    permissionsLens: RequestContextLens<RolePermissions>,
    orderQueries: OrderQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@{ request ->
    val permissions = permissionsLens(request)
    if (!permissions.viewOrder)
        Response(Status.UNAUTHORIZED)
    val idString = request.path("order").orEmpty()
    val id = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val order = orderQueries.FetchOrderViaId().invoke(id)?: return@handler Response(Status.BAD_REQUEST)
    Response(Status.OK).with(htmlView(request) of ShowOrderVM(order))

}

fun deleteOrder(
    permissionsLens: RequestContextLens<RolePermissions>,
    orderQueries: OrderQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@{request->
    val idString = request.path("order").orEmpty()
    val id = UUID.fromString(idString)?: return@handler Response(Status.BAD_REQUEST)
    val permissionsDelete = permissionsLens(request)
    if (!permissionsDelete.deleteOrder)
        Response (Status.UNAUTHORIZED)
    val permissions = permissionsLens(request)
    if (!permissions.listOrders)
        Response(Status.UNAUTHORIZED)
    orderQueries.DeleteOrder().invoke(id)
    val userId= permissions.id
    Response(Status.OK).with(htmlView(request) of ShowBasketVM(orderQueries.FetchOrdersViaUserId().invoke(userId)))

}
fun deleteDishFromOrder(
    permissionsLens: RequestContextLens<RolePermissions>,
    orderQueries: OrderQueries,
    htmlView: ContextAwareViewRender,
):HttpHandler = handler@{request->
    val permissions = permissionsLens(request)
    if (!permissions.viewOrder)
        Response(Status.UNAUTHORIZED)
    val idString = request.path("order").orEmpty()
    val id = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val order = orderQueries.FetchOrderViaId().invoke(id)?: return@handler Response(Status.BAD_REQUEST)
    val indexString = request.path("dish").orEmpty()
    val index= indexString.toIntOrNull()?: return@handler Response(Status.BAD_REQUEST)
    val newOrder= orderQueries.DeleteDish().invoke(order,index)
    orderQueries.UpdateOrder().invoke(newOrder)
    Response(Status.OK).with(htmlView(request) of ShowOrderVM(newOrder))
}

fun editStatusByUser(
    permissionsLens: RequestContextLens<RolePermissions>,
    orderQueries: OrderQueries,
    htmlView: ContextAwareViewRender,
):HttpHandler= handler@{request->
    val permissions = permissionsLens(request)
    if (!permissions.listOrders)
        Response(Status.UNAUTHORIZED)
    val userId = permissions.id
    val idString = request.form().findSingle("index").orEmpty()
    val index= idString.toIntOrNull()?: return@handler Response(Status.BAD_REQUEST)
    orderQueries.EditStatus().invoke(index,"В обработке", userId)
    Response(Status.OK).with(htmlView(request) of ShowBasketVM(orderQueries.FetchOrdersViaUserId().invoke(userId)))
}