package ru.ac.uniyar

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

@Suppress("LongParameterList")
class Router (
    private val pingHandler: HttpHandler,
    private val redirectToHomePage: HttpHandler,

    private val registerFormH: HttpHandler,
    private val registerH: HttpHandler,
    private val registerOperatorH:HttpHandler,

    private val loginFormH: HttpHandler,
    private val loginH: HttpHandler,
    private val logOutH: HttpHandler,

    private val profileH:HttpHandler,
    private val editProfileFormH: HttpHandler,
    private val editProfileH: HttpHandler,

    private val ordersH:HttpHandler,
    private val orderH: HttpHandler,

    private val homePageH: HttpHandler,
    private val addRestaurantFormH: HttpHandler,
    private val addRestaurantH: HttpHandler,

    private val editRestaurantH: HttpHandler,
    private val editRestaurantFormH: HttpHandler,
    private val deleteRestaurantH: HttpHandler,

    private val reviewsH:HttpHandler,
    private val reviewFormH:HttpHandler,
    private val addReviewH:HttpHandler,

    private val deleteDishH: HttpHandler,
    private val editAvailabilityH:HttpHandler,
    private val addDishToOrderH:HttpHandler,

    private val restaurantH: HttpHandler,
    private val addDishFormH: HttpHandler,
    private val addDishH: HttpHandler,

    private val editDishFormH: HttpHandler,
    private val editDishH: HttpHandler,

    private val basketH:HttpHandler,
    private val editStatusByUserH:HttpHandler,
    private val orderFromBasketH: HttpHandler,
    private val deleteOrderH: HttpHandler,
    private val deleteDishFromOrderH:HttpHandler,

    private val orderForOperatorH:HttpHandler,
    private val editStatusByOperatorH:HttpHandler,

    private val staticFilesHandler: RoutingHttpHandler,
) {
    operator fun invoke(): RoutingHttpHandler = routes(
        "/ping" bind Method.GET to pingHandler,
        "/" bind Method.GET to redirectToHomePage,

        "/register" bind Method.GET to registerFormH,
        "/register" bind Method.POST to registerH,
        "/registerOperator" bind Method.GET to registerOperatorH,
        "/registerOperator" bind Method.POST to registerH,

        "/login" bind Method.GET to loginFormH,
        "/login" bind Method.POST to loginH,
        "/logout" bind Method.GET to logOutH,

        "/profile" bind Method.GET to profileH,
        "/profile/edit" bind Method.GET to editProfileFormH,
        "/profile/edit" bind Method.POST to editProfileH,

        "/profile/orders" bind Method.GET to ordersH,
        "/profile/orders/{order}" bind Method.GET to orderH,

        "/restaurants" bind Method.GET to homePageH,
        "/restaurants/new" bind Method.GET to addRestaurantFormH,
        "/restaurants/new" bind Method.POST to addRestaurantH,

        "/{restaurant}/edit" bind Method.GET to editRestaurantFormH,
        "/{restaurant}/edit" bind Method.POST to editRestaurantH,
        "/{restaurant}/delete" bind Method.GET to deleteRestaurantH,

        "/reviews/{restaurant}" bind Method.GET to reviewsH,
        "/{restaurant}/addReview" bind Method.GET to reviewFormH,
        "/{restaurant}/addReview" bind Method.POST to addReviewH,

        "/{restaurant}/{dish}/delete" bind Method.GET to deleteDishH,
        "/{restaurant}/{dish}/availability" bind Method.GET to editAvailabilityH,
        "/{restaurant}/{dish}/dishToOrder" bind Method.GET to addDishToOrderH,

        "/{restaurant}/ListOfDishes" bind Method.GET to restaurantH,
        "/{restaurant}/ListOfDishes/new" bind Method.GET to addDishFormH,
        "/{restaurant}/ListOfDishes/new" bind Method.POST to addDishH,

        "/{restaurant}/{dish}/edit" bind Method.GET to editDishFormH,
        "/{restaurant}/{dish}/edit" bind Method.POST to editDishH,

        "/basket" bind Method.GET to basketH,
        "/basket" bind Method.POST to editStatusByUserH,
        "/basket/{order}" bind Method.GET to orderFromBasketH,
        "/basket/delete/{order}" bind Method.GET to deleteOrderH,
        "/basket/{order}/{dish}/delete" bind Method.GET to deleteDishFromOrderH,

        "/orders" bind Method.GET to ordersH,
        "/orders/{order}" bind Method.GET to orderForOperatorH,
        "/orders/{order}" bind Method.POST to editStatusByOperatorH,

        staticFilesHandler,
    )
}