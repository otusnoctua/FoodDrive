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
    curUserLens: RequestContextLens<User?>,
    htmlView: ContextAwareViewRender,
    storeHolder: StoreHolder,

) {
    val pingHandler = PingHandler()
    val redirectToHomePage = RedirectToHomePage()
    val homePageH = HomePageH(
        permissionLens,
        storeHolder.restaurantQueries,
        storeHolder.dishQueries,
        htmlView
    )
    val restaurantH = RestaurantH(
        permissionLens,
        storeHolder.dishQueries,
        storeHolder.restaurantQueries,
        htmlView
    )
    val registerFormH = RegisterFormH(
        htmlView)
    val registerH = RegisterH(
        storeHolder.addUserQ,
        htmlView,
        permissionLens,
    storeHolder.restaurantQueries,)
    val loginFormH = LoginFormH(
        htmlView)
    val loginH = LoginH(
        storeHolder.authenticateUserViaLoginQ,
        htmlView, jwtTools)
    val logOutH = LogOutH()
    val addRestaurantFormH = AddRestaurantFormH(
        permissionLens,
        htmlView)
    val addRestaurantH = AddRestaurantH(
        permissionLens,
        storeHolder.restaurantQueries,
        htmlView)
    val deleteRestaurantH = DeleteRestaurantH(
        permissionLens,
        storeHolder.restaurantQueries,
        storeHolder.dishQueries)
    val editRestaurantH = EditRestaurantH(
        permissionLens,
        storeHolder.restaurantQueries,
        htmlView)
    val editRestaurantFormH = EditRestaurantFormH(
        permissionLens,
        htmlView)
    val addDishH = AddDishH(
        permissionLens,
        storeHolder.restaurantQueries,
        storeHolder.dishQueries,
        htmlView)
    val deleteDishH = DeleteDishH(
        permissionLens,
        storeHolder.dishQueries,
    )
    val editDishH = EditDishH(
        permissionLens,
        storeHolder.dishQueries,
        storeHolder.restaurantQueries,
        htmlView)
    val editDishFormH = EditDishFormH(
        permissionLens,
        storeHolder.restaurantQueries,
        htmlView)
    val addDishFormH = AddDishFormH(
        permissionLens,
        storeHolder.restaurantQueries,
        htmlView)
    val basketH = BasketH(
        permissionLens,
        curUserLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val addDishToOrderH = AddDishToOrderH(
        permissionLens,
        curUserLens,
        htmlView,
        storeHolder.orderQueries,
        storeHolder.dishQueries,
        storeHolder.restaurantQueries,
    )
    val orderH = OrderH(
        permissionLens,
        curUserLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val deleteOrderH = DeleteOrderH(
        permissionLens,
        curUserLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val deleteDishFromOrderH = DeleteDishFromOrderH(
        permissionLens,
        curUserLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val editStatusByUserH = EditStatusByUserH(
        permissionLens,
        curUserLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val reviewsH = ReviewsH(
        permissionLens,
        storeHolder.reviewQueries,
        storeHolder.restaurantQueries,
        htmlView
    )
    val reviewFormH = ReviewFormH(
        permissionLens,
        storeHolder.reviewQueries,
        htmlView
    )

    val addReviewH = AddReviewH(
        permissionLens,
        storeHolder.reviewQueries,
        storeHolder.restaurantQueries,
        htmlView
    )
    val ordersH= OrdersH(
        permissionLens,
        curUserLens,
        storeHolder.orderQueries,
        htmlView,
    )
    val orderForOperatorH = OrderForOperatorH(
        permissionLens,
        storeHolder.orderQueries,
        storeHolder.userQueries,
        storeHolder.restaurantQueries,
        htmlView
    )
    val editStatusByOperatorH = EditStatusByOperatorH(
        permissionLens,
        storeHolder.orderQueries,
    )
    val editAvailabilityH = EditAvailabilityH(
        permissionLens,
        storeHolder.dishQueries,
    )
    val registerOperatorH = RegisterOperatorH(
        htmlView,
        storeHolder.restaurantQueries,
    )
}