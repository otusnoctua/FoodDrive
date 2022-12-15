package ru.ac.uniyar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.deleteAll
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import ru.ac.uniyar.domain.*
import ru.ac.uniyar.queries.*
import java.nio.file.Path
import java.time.LocalDateTime

class DatabaseTest {

    private val database = Database.connect(
        url = "jdbc:mysql://127.0.0.1:3305/fooddrive",
        driver = "com.mysql.jdbc.Driver",
        user = "root",
        password = "12345"
    )

    private val store = Store(database)
    private val settings = Settings(Path.of("settings.json"))

    val userQueries = UserQueries(store, settings, store.userRepository)
    val reviewQueries = ReviewQueries(store.reviewRepository, store, database)
    val restaurantQueries = RestaurantQueries(store.restaurantRepository, reviewQueries, store)
    val dishQueries = DishQueries(store.dishRepository, store)
    val orderQueries = OrderQueries(store.userRepository, store.orderRepository, store)
    val fetchUserQueries = FetchUserQ(store.userRepository)
    val fetchPermissionsQueries = FetchPermissionsQ(store.rolePermissionsRepository)
    val authenticateUserViaLoginQueries = AuthenticateUserViaLoginQ(settings, store.userRepository)

    /* Проверяет добавление пользователя и соответствие роли клиента, если пользователю не назначен ресторан */
    @Test
    fun addNewUser() {
        database.useTransaction {
            database.deleteAll(Users)
            userQueries.AddUserQ().invoke(
                "Alice",
                123,
                "alice@example.com",
                "hashed_password_here",
                null
            )
            assertEquals(database.users.find { it.username eq "Alice" }?.username, "Alice")
            assertEquals(database.users.find { it.username eq "Alice" }?.roleId, 1)
            database.deleteAll(Users)
        }

    }
    @Test
    fun addNewRestaurant(){
        database.useTransaction {
            database.deleteAll(Restaurants)
            restaurantQueries.AddRestaurantQ().invoke(
                "kfc",
                "link"
            )
            assertNotEquals(database.restaurants.find { it.restaurant_name eq "kfc" }, null)
            database.deleteAll(Restaurants)
        }
    }
    @Test
    fun addNewDish(){
        database.useTransaction {
            database.deleteAll(Dishes)
            database.deleteAll(Restaurants)
                restaurantQueries.AddRestaurantQ().invoke(
                "kfc",
                "link"
            )
            val restaurantToAdd = database.restaurants.find { it.restaurant_name eq "kfc" }!!
            dishQueries.AddDishQ().invoke(
                restaurantToAdd,
                "Бургер",
                false,
                "Булка, курица",
                "1",
                true,
                100,
                "link"
            )
            assertNotEquals(database.dishes.find { it.dishName eq "Бургер" }, null)
            database.deleteAll(Dishes)
            database.deleteAll(Restaurants)
        }
    }
    @Test
    fun addNewReview() {
        database.useTransaction {
            database.deleteAll(Reviews)
            database.deleteAll(Users)
            database.deleteAll(Restaurants)
            userQueries.AddUserQ().invoke(
                "Alice",
                123,
                "alice@example.com",
                "hashed_password_here",
                null
            )
            val userToAdd = database.users.find { it.username eq "Alice" }!!
            restaurantQueries.AddRestaurantQ().invoke(
                "kfc",
                "link"
            )
            val restaurantToAdd = database.restaurants.find { it.restaurant_name eq "kfc" }!!
            val reviewToAdd = Review {
                user = userToAdd
                restaurant = restaurantToAdd
                reviewText = "Отзыв"
                restaurantRating = 4
                addTime = LocalDateTime.now()
            }
            database.deleteAll(Reviews)
            reviewQueries.AddReviewQ().invoke(
                reviewToAdd
            )
            assertNotEquals(database.reviews.find { it.review_text eq "Отзыв" }, null)
            val reviewId = database.reviews.find { it.review_text eq "Отзыв" }?.id
            database.deleteAll(Reviews)
            database.deleteAll(Users)
            database.deleteAll(Restaurants)
        }
    }

}