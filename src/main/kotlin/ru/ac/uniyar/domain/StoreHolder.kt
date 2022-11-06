package ru.ac.uniyar.domain

import ru.ac.uniyar.queries.ListOfDishesQuery
import ru.ac.uniyar.queries.ListOfRestaurantsQuery
import ru.ac.uniyar.queries.RestaurantQuery
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


}