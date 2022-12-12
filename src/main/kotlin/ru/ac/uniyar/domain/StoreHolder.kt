package ru.ac.uniyar.domain

import org.ktorm.database.Database
import ru.ac.uniyar.queries.*
import java.nio.file.Path

class StoreHolder(
    settingsPath: Path,
    database: Database
) {
    val store = Store(database)
    val settings = Settings(settingsPath)
    val fetchUserQ = FetchUserQ(store.userRepository)
    val authenticateUserViaLoginQ = AuthenticateUserViaLoginQ(settings, store.userRepository)
    val fetchPermissionsViaQuery = FetchPermissionsQ(store.rolePermissionsRepository)
    val dishQueries = DishQueries(store.dishRepository, store)
    val orderQueries = OrderQueries(store.userRepository, store.orderRepository, store)
    val reviewQueries = ReviewQueries(store.reviewRepository, store, database)
    val restaurantQueries = RestaurantQueries(store.restaurantRepository, reviewQueries, store)
    val userQueries= UserQueries(store, settings, store.userRepository)
}