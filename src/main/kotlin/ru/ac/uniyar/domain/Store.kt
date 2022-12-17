package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.map

class Store(
    private val db: Database
) {

    companion object{
        val db = Database.connect(
            url = "jdbc:mysql://127.0.0.1:3306/fooddrive",
            driver = "com.mysql.jdbc.Driver",
            user = "root",
            password = "12345"
        )
        fun getDishesViaOrderId(id: Int) : List<Dish> {
            db.useTransaction {
                val dishIds = db.order_dishes.filter { it.order_id eq id }.map { it.dishId }
                return dishIds.map { dishId -> db.dishes.find { it.id eq dishId }!! }
            }
        }
    }

    val rolePermissionsRepository: RolePermissionsRepository = RolePermissionsRepository(emptyList())
    val dishRepository: DishRepository = DishRepository(db)
    val restaurantRepository: RestaurantRepository = RestaurantRepository(db)
    val orderRepository: OrderRepository = OrderRepository(db)
    val reviewRepository: ReviewRepository = ReviewRepository(db)
    val userRepository:UserRepository = UserRepository(db)
}