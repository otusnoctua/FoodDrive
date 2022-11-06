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
    private val ShowUserForm: HttpHandler,
    private val AddUser: HttpHandler,
    private val ShowLoginFormHandler: HttpHandler,
    private val AuthenticateUser: HttpHandler,
    private val LogOutUser: HttpHandler,

    private val staticFilesHandler: RoutingHttpHandler,
) {
    operator fun invoke(): RoutingHttpHandler = routes(
        "/ping" bind Method.GET to pingHandler,
        "/" bind Method.GET to redirectToRestaurants,

        "/register" bind Method.GET to ShowUserForm,
        "/register" bind Method.POST to AddUser,

        "/login" bind Method.GET to ShowLoginFormHandler,
        "/login" bind Method.POST to AuthenticateUser,
        "/logout" bind Method.GET to LogOutUser,

        "/restaurants" bind Method.GET to showListOfRestaurants,
        "/{restaurants}/ListOfDishes" bind Method.GET to showListOfDishes,


        staticFilesHandler,
    )
}