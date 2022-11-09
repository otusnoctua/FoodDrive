package ru.ac.uniyar

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.ac.uniyar.handlers.editRestaurant
import ru.ac.uniyar.handlers.showEditRestaurantForm

@Suppress("LongParameterList")
class Router (
    private val pingHandler: HttpHandler,
    private val redirectToRestaurants: HttpHandler,
    private val showListOfRestaurants: HttpHandler,
    private val showListOfDishes: HttpHandler,
    private val showUserForm: HttpHandler,
    private val addUser: HttpHandler,
    private val showLoginFormHandler: HttpHandler,
    private val authenticateUser: HttpHandler,
    private val logOutUser: HttpHandler,
    private val showRestaurantForm: HttpHandler,
    private val addRestaurant: HttpHandler,
    private val deleteRestaurant: HttpHandler,
    private val editRestaurant: HttpHandler,
    private val showEditRestaurantForm: HttpHandler,

    private val staticFilesHandler: RoutingHttpHandler,
) {
    operator fun invoke(): RoutingHttpHandler = routes(
        "/ping" bind Method.GET to pingHandler,
        "/" bind Method.GET to redirectToRestaurants,

        "/register" bind Method.GET to showUserForm,
        "/register" bind Method.POST to addUser,

        "/login" bind Method.GET to showLoginFormHandler,
        "/login" bind Method.POST to authenticateUser,
        "/logout" bind Method.GET to logOutUser,

        "/{restaurant}/delete" bind Method.GET to deleteRestaurant,
        "/restaurants" bind Method.GET to showListOfRestaurants,
        "/restaurants/new" bind Method.GET to showRestaurantForm,
        "/restaurants/new" bind Method.POST to addRestaurant,

        "/{restaurant}/edit" bind Method.GET to showEditRestaurantForm,
        "/{restaurant}/edit" bind Method.POST to editRestaurant,

        "/{restaurants}/ListOfDishes" bind Method.GET to showListOfDishes,


        staticFilesHandler,
    )
}