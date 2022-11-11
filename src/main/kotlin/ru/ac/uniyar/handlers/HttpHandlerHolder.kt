package ru.ac.uniyar.handlers

import org.http4k.lens.RequestContextLens
import ru.ac.uniyar.domain.JwtTools
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.domain.StoreHolder
import ru.ac.uniyar.models.template.ContextAwareViewRender

class HttpHandlerHolder(
    jwtTools: JwtTools,
    permissionLens: RequestContextLens<RolePermissions>,
    htmlView: ContextAwareViewRender,
    storeHolder: StoreHolder,

) {
    val pingHandler = PingHandler()
    val redirectToRestaurants = RedirectToRestaurants()
    val showListOfRestaurants = showListOfRestaurants(
        permissionLens,
        storeHolder.listOfRestaurantsQuery,
        storeHolder.dishQueries,
        htmlView
    )
    val showListOfDishes = showListOfDishes(
        permissionLens,
        storeHolder.dishQueries,
        storeHolder.restaurantQuery,
        htmlView
    )
    val showUserForm = ShowUserForm(
        htmlView)
    val addUser = AddUser(
        storeHolder.addUserQuery,
        htmlView)
    val showLoginFormHandler = ShowLoginFormHandler(
        htmlView)
    val authenticateUser = AuthenticateUser(
        storeHolder.authenticateUserViaLoginQuery,
        htmlView, jwtTools)
    val logOutUser = LogOutUser()
    val showRestaurantForm = showRestaurantForm(
        permissionLens,
        htmlView)
    val addRestaurant = addRestaurant(
        permissionLens,
        storeHolder.addRestaurantQuery,
        htmlView)
    val deleteRestaurant = deleteRestaurant(
        permissionLens,
        storeHolder.restaurantQuery,
        storeHolder.deleteRestaurantQuery,
        storeHolder.dishQueries)
    val editRestaurant = editRestaurant(
        permissionLens,
        storeHolder.restaurantQuery,
        storeHolder.editRestaurantQuery,
        htmlView)
    val showEditRestaurantForm = showEditRestaurantForm(
        permissionLens,
        htmlView)
    val addDish = addDish(
        permissionLens,
        storeHolder.restaurantQuery,
        storeHolder.addDishQuery,
        htmlView)
    val deleteDish = deleteDish(
        permissionLens,
        storeHolder.dishQueries,
        storeHolder.deleteDishQuery)
    val editDish = editDish(
        permissionLens,
        storeHolder.dishQueries,
        storeHolder.restaurantQuery,
        storeHolder.editDishQuery,
        htmlView)
    val showEditDishForm = showEditDishForm(
        permissionLens,
        storeHolder.restaurantQuery,
        htmlView)
    val showDishForm = showDishForm(
        permissionLens,
        storeHolder.restaurantQuery,
        htmlView)
    val showBasket = showBasket(
        permissionLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val addDishToOrder = addDishToOrder(
        permissionLens,
        htmlView,
        storeHolder.orderQueries,
        storeHolder.dishQueries,
        storeHolder.restaurantQuery,
    )
    val showOrder = showOrder(
        permissionLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val deleteOrder = deleteOrder(
        permissionLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val deleteDishFromOrder = deleteDishFromOrder(
        permissionLens,
        storeHolder.orderQueries,
        htmlView,
    )
}