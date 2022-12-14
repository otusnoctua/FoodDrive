package ru.ac.uniyar.domain

data class RolePermissions(
    val id: Int,
    val name: String,
    val listOrders: Boolean,
    val listUsers: Boolean,
    val listRestaurants: Boolean,
    val listDishes: Boolean,
    val listReviews: Boolean,
    val createOrder: Boolean,
    val createUser: Boolean,
    val createRestaurant: Boolean,
    val createDish: Boolean,
    val createReview: Boolean,
    val viewOrder: Boolean,
    val viewUser: Boolean,
    val viewRestaurant: Boolean,
    val viewDish: Boolean,
    val viewReview: Boolean,
    val editOrder: Boolean,
    val editUser: Boolean,
    val editRestaurant: Boolean,
    val editDish: Boolean,
    val editReview: Boolean,
    val deleteOrder: Boolean,
    val deleteUser: Boolean,
    val deleteRestaurant: Boolean,
    val deleteDish: Boolean,
    val deleteReview: Boolean,
    val viewBasket: Boolean,
    val editStopList: Boolean,
    val createOperator: Boolean
) {
    companion object {
        val ANONYMOUS_ROLE = RolePermissions(
            id = 0,
            name = "Гость",
            listOrders = true,
            listUsers = false,
            listRestaurants = true,
            listDishes = true,
            listReviews = true,
            createOrder = false,
            createUser = true,
            createRestaurant = false,
            createDish = false,
            createReview = false,
            viewOrder = false,
            viewUser = false,
            viewRestaurant = true,
            viewDish = true,
            viewReview = true,
            editOrder = false,
            editUser = false,
            editRestaurant = false,
            editDish = false,
            editReview = false,
            deleteOrder = true,
            deleteUser = false,
            deleteRestaurant = false,
            deleteDish = false,
            deleteReview = false,
            viewBasket = false,
            editStopList = false,
            createOperator = false
        )

        val CLIENT_ROLE = RolePermissions(
            id = 1,
            name = "Заказчик",
            listOrders = false,
            listUsers = false,
            listRestaurants = true,
            listDishes = true,
            listReviews = true,
            createOrder = true,
            createUser = false,
            createRestaurant = false,
            createDish = false,
            createReview = true,
            viewOrder = true,
            viewUser = true,
            viewRestaurant = true,
            viewDish = true,
            viewReview = true,
            editOrder = true,
            editUser = true,
            editRestaurant = false,
            editDish = false,
            editReview = true,
            deleteOrder = true,
            deleteUser = false,
            deleteRestaurant = false,
            deleteDish = false,
            deleteReview = false,
            viewBasket = true,
            editStopList = false,
            createOperator = false
        )

        val OPERATOR_ROLE = RolePermissions(
            id = 2,
            name = "Оператор",
            listOrders = true,
            listUsers = true,
            listRestaurants = true,
            listDishes = true,
            listReviews = true,
            createOrder = false,
            createUser = false,
            createRestaurant = false,
            createDish = false,
            createReview = false,
            viewOrder = true,
            viewUser = true,
            viewRestaurant = true,
            viewDish = true,
            viewReview = true,
            editOrder = true,
            editUser = false,
            editRestaurant = false,
            editDish = false,
            editReview = false,
            deleteOrder = true,
            deleteUser = false,
            deleteRestaurant = false,
            deleteDish = false,
            deleteReview = false,
            viewBasket = false,
            editStopList = true,
            createOperator = false
        )

        val ADMIN_ROLE = RolePermissions(
            id = 3,
            name = "Администратор",
            listOrders = true,
            listUsers = true,
            listRestaurants = true,
            listDishes = true,
            listReviews = true,
            createOrder = true,
            createUser = true,
            createRestaurant = true,
            createDish = true,
            createReview = true,
            viewOrder = true,
            viewUser = true,
            viewRestaurant = true,
            viewDish = true,
            viewReview = true,
            editOrder = true,
            editUser = true,
            editRestaurant = true,
            editDish = true,
            editReview = true,
            deleteOrder = true,
            deleteUser = true,
            deleteRestaurant = true,
            deleteDish = true,
            deleteReview = true,
            viewBasket = true,
            editStopList = true,
            createOperator = true
        )
    }
}
