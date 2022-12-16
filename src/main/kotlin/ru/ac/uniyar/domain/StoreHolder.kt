package ru.ac.uniyar.domain

import ru.ac.uniyar.queries.*
import java.nio.file.Path

class StoreHolder(
    settingsPath: Path,
) {
    val store = Store()
    val settings = Settings(settingsPath)
    val fetchUserQ = FetchUserQ(store.userRepository)
    val authenticateUserViaLoginQ = AuthenticateUserViaLoginQ(settings, store.userRepository)
    val fetchPermissionsViaQuery = FetchPermissionsQ(store.rolePermissionsRepository)
    val dishQueries = DishQueries(store.dishRepository)
    val orderQueries = OrderQueries(store.orderRepository)
    val reviewQueries = ReviewQueries(store.reviewRepository)
    val restaurantQueries = RestaurantQueries(store.restaurantRepository, reviewQueries)
    val userQueries= UserQueries(settings, store.userRepository)
}