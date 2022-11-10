package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.lens.RequestContextLens
import org.http4k.routing.path
import ru.ac.uniyar.domain.EMPTY_UUID
import ru.ac.uniyar.domain.Order
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.ShowBasketVM
import ru.ac.uniyar.models.ShowListOfDishesVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.ListOfDishesQuery
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
    val user_id = permissions.id


    Response(Status.OK).with(htmlView(request) of ShowBasketVM(orderQuery.fetchOrdersViaUser_Id(user_id)))

}


fun addDishToOrder(
    permissionsLens: RequestContextLens<RolePermissions>,
    htmlView: ContextAwareViewRender,
    orderQuery: OrderQueries,
    listOfDishesQuery: ListOfDishesQuery,
    restaurantQuery: RestaurantQuery,
): HttpHandler =handler@ { request ->
    val permissions = permissionsLens(request)
    val user_id= permissions.id
    val params= request.form()
    val idString= params.findSingle("id").orEmpty()
    val id= UUID.fromString(idString)
    val restaurantIdString = request.path("restaurants").orEmpty()
    val restaurant_id = UUID.fromString(restaurantIdString) ?: return@handler Response(Status.BAD_REQUEST)

    if(!orderQuery.check(user_id)){
        val dishes = mutableListOf<UUID>()
        dishes.add(id)
        val order= Order(
            EMPTY_UUID,
            user_id,
            restaurant_id,
            "В ожидании",
            LocalDateTime.now(),
            dishes
        )
        orderQuery.add(order)
    }else{
        orderQuery.update( orderQuery.addDish(user_id,id))
    }
    val model =
        restaurantQuery.invoke(restaurant_id)?.let { ShowListOfDishesVM(listOfDishesQuery.invoke(restaurant_id), it) }
    Response(Status.OK).with(htmlView(request) of model!!)

}