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
        curUserLens,
        storeHolder.restaurantQueries,
        storeHolder.reviewQueries,
        storeHolder.dishQueries,
        htmlView,
    )

    val restaurantH = RestaurantH(
        permissionLens,
        storeHolder.dishQueries,
        storeHolder.restaurantQueries,
        storeHolder.reviewQueries,
        htmlView,
    )

    val registerFormH = RegisterFormH(
        htmlView,
    )

    val registerH = RegisterH(
        storeHolder.userQueries,
        permissionLens,
        storeHolder.store.restaurantRepository,
        storeHolder.restaurantQueries,
        htmlView,
        jwtTools,
    )
    val loginFormH = LoginFormH(
        htmlView,
    )

    val loginH = LoginH(
        storeHolder.authenticateUserViaLoginQ,
        htmlView,
        jwtTools,
    )
    val logOutH = LogOutH()

    val addRestaurantFormH = AddRestaurantFormH(
        permissionLens,
        htmlView,
    )

    val addRestaurantH = AddRestaurantH(
        permissionLens,
        storeHolder.restaurantQueries,
        htmlView,
    )

    val deleteRestaurantH = DeleteRestaurantH(
        permissionLens,
        storeHolder.restaurantQueries,
        storeHolder.dishQueries,
    )

    val editRestaurantH = EditRestaurantH(
        permissionLens,
        storeHolder.restaurantQueries,
        htmlView,
    )

    val editRestaurantFormH = EditRestaurantFormH(
        permissionLens,
        htmlView,
    )

    val addDishH = AddDishH(
        permissionLens,
        storeHolder.restaurantQueries,
        storeHolder.dishQueries,
        htmlView,
    )

    val deleteDishH = DeleteDishH(
        permissionLens,
        storeHolder.dishQueries,
        storeHolder.restaurantQueries,
    )

    val editDishH = EditDishH(
        permissionLens,
        storeHolder.dishQueries,
        htmlView,
    )

    val editDishFormH = EditDishFormH(
        permissionLens,
        storeHolder.dishQueries,
        htmlView,
    )

    val addDishFormH = AddDishFormH(
        permissionLens,
        storeHolder.restaurantQueries,
        htmlView,
    )

    val addDishToOrderH = AddDishToOrderH(
        permissionLens,
        curUserLens,
        storeHolder.orderQueries,
        storeHolder.dishQueries,
    )

    val orderH = OrderH(
        permissionLens,
        storeHolder.orderQueries,
        htmlView,
    )

    val basketH = BasketH(
        permissionLens,
        curUserLens,
        storeHolder.orderQueries,
        htmlView,
    )

    val deleteOrderH = DeleteOrderH(
        permissionLens,
        curUserLens,
        storeHolder.orderQueries,
    )

    val deleteDishFromOrderH = DeleteDishFromOrderH(
        permissionLens,
        curUserLens,
        storeHolder.orderQueries,
        storeHolder.dishQueries,
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
        curUserLens,
        storeHolder.reviewQueries,
        storeHolder.restaurantQueries,
        storeHolder.orderQueries,
        htmlView,
    )

    val reviewFormH = ReviewFormH(
        permissionLens,
        htmlView,
    )

    val addReviewH = AddReviewH(
        permissionLens,
        curUserLens,
        storeHolder.reviewQueries,
        storeHolder.restaurantQueries,
        storeHolder.orderQueries,
        htmlView,
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
        htmlView,
    )

    val editStatusByOperatorH = EditStatusByOperatorH(
        permissionLens,
        curUserLens,
        storeHolder.orderQueries,
    )

    val editAvailabilityH = EditAvailabilityH(
        permissionLens,
        curUserLens,
        storeHolder.dishQueries,
        storeHolder.restaurantQueries,
    )

    val registerOperatorFormH = RegisterOperatorFormH(
        htmlView,
        storeHolder.restaurantQueries,
    )

    val profileH = ProfileH(
        permissionLens,
        curUserLens,
        htmlView,
    )

    val editProfileFormH = EditProfileFormH(
        permissionLens,
        curUserLens,
        htmlView,
    )

    val operatorOrdersH = OperatorOrdersH(
        permissionLens,
        curUserLens,
        storeHolder.orderQueries,
        htmlView,
    )

    val editProfileH = EditProfileH(
        permissionLens,
        curUserLens,
        storeHolder.userQueries,
        htmlView,
    )

    val orderFromBasketH = OrderFromBasketH(
        permissionLens,
        curUserLens,
        storeHolder.orderQueries,
        htmlView,
    )
}