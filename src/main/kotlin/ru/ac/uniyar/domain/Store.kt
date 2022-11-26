package ru.ac.uniyar.domain

import org.ktorm.database.Database

class Store(
    database: Database
    ) {

    val rolePermissionsRepository: RolePermissionsRepository = RolePermissionsRepository(emptyList())
    val dishRepository: DishRepository
    val restaurantRepository: RestaurantRepository
    val orderRepository: OrderRepository
    val reviewRepository: ReviewRepository
    val userRepository:UserRepository


    init {
        dishRepository = DishRepository(database)
        restaurantRepository = RestaurantRepository(database)
        orderRepository = OrderRepository(database)
        reviewRepository = ReviewRepository(database)
        userRepository = UserRepository(database)
    }


}