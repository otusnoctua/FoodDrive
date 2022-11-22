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
        storeHolder.restaurantQueries,
        storeHolder.dishQueries,
        htmlView
    )
    val showListOfDishes = showListOfDishes(
        permissionLens,
        storeHolder.dishQueries,
        storeHolder.restaurantQueries,
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
        storeHolder.restaurantQueries,
        htmlView)
    val deleteRestaurant = deleteRestaurant(
        permissionLens,
        storeHolder.restaurantQueries,
        storeHolder.dishQueries)
    val editRestaurant = editRestaurant(
        permissionLens,
        storeHolder.restaurantQueries,
        htmlView)
    val showEditRestaurantForm = showEditRestaurantForm(
        permissionLens,
        htmlView)
    val addDish = addDish(
        permissionLens,
        storeHolder.restaurantQueries,
        storeHolder.dishQueries,
        htmlView)
    val deleteDish = deleteDish(
        permissionLens,
        storeHolder.dishQueries,
    )
    val editDish = editDish(
        permissionLens,
        storeHolder.dishQueries,
        storeHolder.restaurantQueries,
        htmlView)
    val showEditDishForm = showEditDishForm(
        permissionLens,
        storeHolder.restaurantQueries,
        htmlView)
    val showDishForm = showDishForm(
        permissionLens,
        storeHolder.restaurantQueries,
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
        storeHolder.restaurantQueries,
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
    val editStatusByUser = editStatusByUser(
        permissionLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val showReviewList = showReviewList(
        permissionLens,
        storeHolder.reviewQueries,
        storeHolder.restaurantQueries,
        htmlView
    )
    val showReviewForm = showReviewForm(
        permissionLens,
        storeHolder.reviewQueries,
        htmlView
    )

    val addReviewToList = addReviewToList(
        permissionLens,
        storeHolder.reviewQueries,
        storeHolder.restaurantQueries,
        htmlView
    )
}