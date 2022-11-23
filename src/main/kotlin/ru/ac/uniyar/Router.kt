package ru.ac.uniyar

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

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
    private val deleteDish: HttpHandler,
    private val showDishForm: HttpHandler,
    private val addDish: HttpHandler,
    private val showEditDishForm: HttpHandler,
    private val editDish: HttpHandler,
    private val showBasket:HttpHandler,
    private val addDishToOrder:HttpHandler,
    private val showOrder:HttpHandler,
    private val deleteOrder: HttpHandler,
    private val deleteDishFromOrder:HttpHandler,
    private val editStatusByUser:HttpHandler,
    private val showReviewList:HttpHandler,
    private val showReviewForm:HttpHandler,
    private val addReviewToList:HttpHandler,


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

        "/{restaurant}/addReview" bind Method.GET to showReviewForm,
        "/{restaurant}/addReview" bind Method.POST to addReviewToList,

        "/{restaurant}/{dish}/delete" bind Method.GET to deleteDish,
        "/{restaurant}/ListOfDishes" bind Method.GET to showListOfDishes,
        "/{restaurant}/ListOfDishes" bind Method.POST to addDishToOrder,
        "/{restaurant}/ListOfDishes/new" bind Method.GET to showDishForm,
        "/{restaurant}/ListOfDishes/new" bind Method.POST to addDish,

        "/{restaurant}/{dish}/edit" bind Method.GET to showEditDishForm,
        "/{restaurant}/{dish}/edit" bind Method.POST to editDish,

        "/basket" bind Method.GET to showBasket,
        "/basket" bind Method.POST to editStatusByUser,
        "/basket/{order}/delete/{dish}" bind Method.GET to deleteDishFromOrder,
        "/basket/{order}" bind Method.GET to showOrder,
        "/basket/delete/{order}" bind Method.GET to deleteOrder,

        "/reviews/{restaurant}" bind Method.GET to showReviewList,

        staticFilesHandler,
    )
}