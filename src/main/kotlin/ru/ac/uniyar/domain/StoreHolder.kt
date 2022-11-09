package ru.ac.uniyar.domain

import ru.ac.uniyar.queries.*
import java.nio.file.Path

class StoreHolder(
    documentStorePath: Path,
    settingsPath: Path,
) {
    val store = Store(documentStorePath)
    val settings = Settings(settingsPath)
    val listOfRestaurantsQuery = ListOfRestaurantsQuery(store.restaurantRepository)
    val restaurantQuery = RestaurantQuery(store.restaurantRepository)
    val listOfDishesQuery = ListOfDishesQuery(store.dishRepository)
    val fetchUserViaUserId = FetchUserViaUserId(store.userRepository)
    val addUserQuery = AddUserQuery(store, settings, store.userRepository)
    val authenticateUserViaLoginQuery = AuthenticateUserViaLoginQuery(settings, store.userRepository)
    val fetchPermissionsViaQuery = FetchPermissionsViaIdQuery(store.rolePermissionsRepository)
    val addRestaurantQuery = AddRestaurantQuery(store.restaurantRepository, store)
    val deleteRestaurantQuery = DeleteRestaurantQuery(store)
    val editRestaurantQuery = EditRestaurantQuery(store, store.restaurantRepository)

}