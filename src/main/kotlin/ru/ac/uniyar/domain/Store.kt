package ru.ac.uniyar.domain

import org.ktorm.database.Database

class Store() {

    companion object{
        val db = Database.connect(
            url = "jdbc:mysql://127.0.0.1:3306/fooddrive",
            driver = "com.mysql.jdbc.Driver",
            user = "root",
            password = "12345"
        )
    }

    val rolePermissionsRepository: RolePermissionsRepository = RolePermissionsRepository(emptyList())
    val dishRepository: DishRepository = DishRepository(db)
    val restaurantRepository: RestaurantRepository = RestaurantRepository(db)
    val orderRepository: OrderRepository = OrderRepository(db)
    val reviewRepository: ReviewRepository = ReviewRepository(db)
    val userRepository:UserRepository = UserRepository(db)
}