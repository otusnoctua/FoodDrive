package ru.ac.uniyar.handlers

import org.http4k.lens.RequestContextLens
import ru.ac.uniyar.domain.JwtTools
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.domain.StoreHolder
import ru.ac.uniyar.domain.User
import ru.ac.uniyar.models.template.ContextAwareViewRender

class HttpHandlerHolder(
    jwtTools: JwtTools,
    permissionLens: RequestContextLens<RolePermissions>,
    currentUserLens: RequestContextLens<User?>,
    htmlView: ContextAwareViewRender,
    storeHolder: StoreHolder,

) {
    val pingHandler = PingHandler()
    val redirectToRestaurants = RedirectToRestaurants()
    val showListOfRestaurants = ShowListOfRestaurants(
        permissionLens,
        storeHolder.restaurantQueries,
        storeHolder.dishQueries,
        htmlView
    )
    val showListOfDishes = ShowListOfDishes(
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
    val showRestaurantForm = ShowRestaurantForm(
        permissionLens,
        htmlView)
    val addRestaurant = AddRestaurant(
        permissionLens,
        storeHolder.restaurantQueries,
        htmlView)
    val deleteRestaurant = DeleteRestaurant(
        permissionLens,
        storeHolder.restaurantQueries,
        storeHolder.dishQueries)
    val editRestaurant = EditRestaurant(
        permissionLens,
        storeHolder.restaurantQueries,
        htmlView)
    val showEditRestaurantForm = ShowEditRestaurantForm(
        permissionLens,
        htmlView)
    val addDish = AddDish(
        permissionLens,
        storeHolder.restaurantQueries,
        storeHolder.dishQueries,
        htmlView)
    val deleteDish = DeleteDish(
        permissionLens,
        storeHolder.dishQueries,
    )
    val editDish = EditDish(
        permissionLens,
        storeHolder.dishQueries,
        storeHolder.restaurantQueries,
        htmlView)
    val showEditDishForm = ShowEditDishForm(
        permissionLens,
        storeHolder.restaurantQueries,
        htmlView)
    val showDishForm = ShowDishForm(
        permissionLens,
        storeHolder.restaurantQueries,
        htmlView)
    val showBasket = ShowBasket(
        permissionLens,
        currentUserLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val addDishToOrder = AddDishToOrder(
        permissionLens,
        currentUserLens,
        htmlView,
        storeHolder.orderQueries,
        storeHolder.dishQueries,
        storeHolder.restaurantQueries,
    )
    val showOrder = ShowOrder(
        permissionLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val deleteOrder = DeleteOrder(
        permissionLens,
        currentUserLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val deleteDishFromOrder = DeleteDishFromOrder(
        permissionLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val editStatusByUser = EditStatusByUser(
        permissionLens,
        currentUserLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val showReviewList = ShowReviewList(
        permissionLens,
        storeHolder.reviewQueries,
        storeHolder.restaurantQueries,
        htmlView
    )
    val showReviewForm = ShowReviewForm(
        permissionLens,
        storeHolder.reviewQueries,
        htmlView
    )

    val addReviewToList = AddReviewToList(
        permissionLens,
        currentUserLens,
        storeHolder.reviewQueries,
        storeHolder.restaurantQueries,
        htmlView
    )
}