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

import ru.ac.uniyar.queries.RestaurantQuery
import java.time.LocalDateTime
import java.util.*

fun showBasket(
    permissionsLens: RequestContextLens<RolePermissions>,
    orderQuery: OrderQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = { request ->
    val permissions = permissionsLens(request)
    if (!permissions.listOrders)
        Response(Status.UNAUTHORIZED)

    val user_id = permissions.id

    Response(Status.OK).with(htmlView(request) of ShowBasketVM(orderQuery.fetchOrdersViaUser_Id(user_id)))

}


fun addDishToOrder(
    permissionsLens: RequestContextLens<RolePermissions>,
    htmlView: ContextAwareViewRender,
    orderQuery: OrderQueries,
    dishQueries: DishQueries,
    restaurantQuery: RestaurantQuery,
): HttpHandler = handler@{ request ->
    val permissions = permissionsLens(request)
    if (!permissions.createOrder)
        Response(Status.UNAUTHORIZED)
    val user_id = permissions.id
    val params = request.form()
   val idString = params.findSingle("id").orEmpty()
    val id = UUID.fromString(idString)
    val restaurantIdString = request.path("restaurants").orEmpty()
    val restaurant_id = UUID.fromString(restaurantIdString) ?: return@handler Response(Status.BAD_REQUEST)

    if (!orderQuery.check(user_id)) {
        val dishes = mutableListOf<String>()
        dishes.add(dishQueries.fetchNameDishViaId(id))
        val order = Order(
            EMPTY_UUID,
            user_id,
            restaurant_id,
            "В ожидании",
            LocalDateTime.now(),
            dishes
        )
        orderQuery.add(order)
    } else {
        orderQuery.update(orderQuery.addDish(user_id, id))
    }
    val model =
        restaurantQuery.invoke(restaurant_id)?.let { ShowListOfDishesVM(dishQueries.listOfDishes(restaurant_id), it) }
    Response(Status.OK).with(htmlView(request) of model!!)

}

fun showOrder(
    permissionsLens: RequestContextLens<RolePermissions>,
    orderQuery: OrderQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@{ request ->
    val permissions = permissionsLens(request)
    if (!permissions.viewOrder)
        Response(Status.UNAUTHORIZED)
    val idString = request.path("order").orEmpty()
    val id = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val order = orderQuery.fetchOrderViaId(id)
    Response(Status.OK).with(htmlView(request) of ShowOrderVM(order))

}

fun deleteOrder(
    permissionsLens: RequestContextLens<RolePermissions>,
    orderQuery: OrderQueries,
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
    orderQuery.delete(id)
    val user_id= permissions.id
    Response(Status.OK).with(htmlView(request) of ShowBasketVM(orderQuery.fetchOrdersViaUser_Id(user_id)))

}
fun deleteDishFromOrder(
    permissionsLens: RequestContextLens<RolePermissions>,
    orderQuery: OrderQueries,
    htmlView: ContextAwareViewRender,
):HttpHandler = handler@{request->
    val permissions = permissionsLens(request)
    if (!permissions.viewOrder)
        Response(Status.UNAUTHORIZED)
    val idString = request.path("order").orEmpty()
    val id = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val order = orderQuery.fetchOrderViaId(id)
    val indexString = request.path("dish").orEmpty()
    val index= indexString.toIntOrNull()?: return@handler Response(Status.BAD_REQUEST)
    val newOrder= orderQuery.deleteDish(order,index)
    orderQuery.update(newOrder)
    Response(Status.OK).with(htmlView(request) of ShowOrderVM(newOrder))
}