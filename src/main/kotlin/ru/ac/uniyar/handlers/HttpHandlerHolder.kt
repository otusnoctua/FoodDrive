package ru.ac.uniyar.handlers

import ru.ac.uniyar.domain.StoreHolder
import ru.ac.uniyar.models.template.ContextAwareViewRender

class HttpHandlerHolder(
    htmlView: ContextAwareViewRender,
    storeHolder: StoreHolder,
) {
    val pingHandler = PingHandler()
    val redirectToRestaurants = RedirectToRestaurants()
    val showListOfRestaurants = showListOfRestaurants(
        storeHolder.listOfRestaurantsQuery,
        htmlView
    )
    val showListOfDishes = showListOfDishes(
        storeHolder.listOfDishesQuery,
        storeHolder.restaurantQuery,
        htmlView
    )

}