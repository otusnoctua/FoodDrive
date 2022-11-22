package ru.ac.uniyar.domain

import ru.ac.uniyar.queries.*
import java.nio.file.Path

class StoreHolder(
    documentStorePath: Path,
    settingsPath: Path,
) {
    val store = Store(documentStorePath)
    val settings = Settings(settingsPath)
    val fetchUserViaUserId = FetchUserViaUserId(store.userRepository)
    val addUserQuery = AddUserQuery(store, settings, store.userRepository)
    val authenticateUserViaLoginQuery = AuthenticateUserViaLoginQuery(settings, store.userRepository)
    val fetchPermissionsViaQuery = FetchPermissionsViaIdQuery(store.rolePermissionsRepository)
    val dishQueries = DishQueries(store.dishRepository,store)
    val orderQueries = OrderQueries(store.orderRepository,dishQueries,store)
    val reviewQueries = ReviewQueries(store.reviewRepository)
    val restaurantQueries = RestaurantQueries(store.restaurantRepository,store)


}