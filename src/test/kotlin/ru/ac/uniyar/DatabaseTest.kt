package ru.ac.uniyar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
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
            userQueries.AddUserQ().invoke(
                "Alice",
                123,
                "alice@example.com",
                "hashed_password_here",
                null
            )
            assertEquals(database.users.find { it.username eq "Alice" }?.username, "Alice")
            assertEquals(database.users.find { it.username eq "Alice" }?.roleId, 1)
        }

    }
    @Test
    fun addNewRestaurant(){
        database.useTransaction {
            restaurantQueries.AddRestaurantQ().invoke(
                "kfc",
                "link"
            )
            assertNotEquals(database.restaurants.find { it.restaurant_name eq "kfc" }, null)
        }
    }
    @Test
    fun addNewDish(){
        database.useTransaction {
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
        }
    }
    @Test
    fun addNewReview() {
        database.useTransaction {
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
        }
    }
    @Test
    fun deleteDish(){
        database.useTransaction {
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
            dishQueries.DeleteDishQ().invoke(database.dishes.find { it.dishName eq "Бургер" }!!)
            assertEquals(database.dishes.find { it.dishName eq "Бургер" }, null)
        }
    }
    @Test
    fun deleteRestaurant(){
        database.useTransaction {
            restaurantQueries.AddRestaurantQ().invoke(
                "kfc",
                "link"
            )
            restaurantQueries.DeleteRestaurantQ().invoke(database.restaurants.find { it.restaurant_name eq "kfc" }!!)
            assertEquals(database.restaurants.find { it.restaurant_name eq "kfc" }, null)
        }
    }
    @Test
    fun editRestaurant(){
        database.useTransaction {
            restaurantQueries.AddRestaurantQ().invoke(
                "kfc",
                "link"
            )
            val restaurantId = database.restaurants.find { it.restaurant_name eq "kfc" }?.id!!
            restaurantQueries.EditRestaurantQ().invoke(
                "Burger King",
                database.restaurants.find { it.restaurant_name eq "kfc" }!!)
            assertEquals(restaurantId, database.restaurants.find {it.restaurant_name eq "Burger King"}?.id)
            assertEquals(database.restaurants.find { it.id eq restaurantId }?.restaurantName, "Burger King")
        }
    }
    @Test
    fun editDish(){
        database.useTransaction {
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
            val dishToEdit = database.dishes.find { it.dishName eq "Бургер" }!!
            val dishToEditId = database.dishes.find { it.dishName eq "Бургер" }?.id!!
            dishQueries.EditDishQ().invoke(
                "Салат",
                "Огурец, помидор",
                50,
                "2",
                true,
                false,
                "linkS",
                dishToEdit
            )
            assertEquals(dishToEditId, database.dishes.find { it.dishName eq "Салат" }?.id!!)
            assertEquals(database.dishes.find { it.id eq dishToEditId }?.restaurant, restaurantToAdd)
            assertEquals(database.dishes.find { it.id eq dishToEditId }?.dishName, "Салат")
            assertEquals(database.dishes.find { it.id eq dishToEditId }?.ingredients, "Огурец, помидор")
            assertEquals(database.dishes.find { it.id eq dishToEditId }?.price, 50)
            assertEquals(database.dishes.find { it.id eq dishToEditId }?.dishDescription, "2")
            assertEquals(database.dishes.find { it.id eq dishToEditId }?.vegan, true)
            assertEquals(database.dishes.find { it.id eq dishToEditId }?.availability, false)
            assertEquals(database.dishes.find { it.id eq dishToEditId }?.imageUrl, "linkS")
        }
    }
    @Test
    fun editUser(){
        database.useTransaction {
            userQueries.AddUserQ().invoke(
                "Alice",
                123,
                "alice@example.com",
                "hashed_password_here",
                null
            )
            val userId = database.users.find { it.username eq "Alice" }?.id!!
            val userToEdit = database.users.find { it.username eq "Alice" }!!
            userQueries.EditUserQuery().invoke(
                "Bob",
                321,
                "bob@example.com",
                userToEdit
            )
            assertEquals(userId, database.users.find { it.username eq "Bob" }?.id!!)
            assertEquals(database.users.find { it.id eq userId }?.username, "Bob")
            assertEquals(database.users.find { it.id eq userId }?.phone, 321)
            assertEquals(database.users.find { it.id eq userId }?.email, "bob@example.com")
        }
    }
    @Test
    fun editAvailability(){
        database.useTransaction {
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
            dishQueries.EditAvailability().invoke(
                database.dishes.find { it.dishName eq "Бургер" }!!
            )
            assertEquals(database.dishes.find { it.dishName eq "Бургер" }?.availability, false)
        }
    }
    @Test
    fun fetchRestaurant(){
        database.useTransaction {
            restaurantQueries.AddRestaurantQ().invoke(
                "kfc",
                "link"
            )
            val restaurantId = database.restaurants.find { it.restaurant_name eq "kfc" }?.id!!
            val fetchRestaurant = restaurantQueries.FetchRestaurantQ().invoke(restaurantId)
            assertEquals(database.restaurants.find { it.restaurant_name eq "kfc" }, fetchRestaurant)
        }
    }
    @Test
    fun fetchDish(){
        database.useTransaction {
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
            val dishId = database.dishes.find { it.dishName eq "Бургер" }?.id!!
            val fetchDish = dishQueries.FetchDishQ().invoke(dishId)
            assertEquals(database.dishes.find { it.dishName eq "Бургер" }, fetchDish)
        }
    }
    @Test
    fun fetchUserViaId(){
        database.useTransaction {
            userQueries.AddUserQ().invoke(
                "Alice",
                123,
                "alice@example.com",
                "hashed_password_here",
                null
            )
            val userId = database.users.find { it.username eq "Alice" }?.id!!
            val fetchUser = userQueries.FetchUserViaId().invoke(userId)
            assertEquals(database.users.find { it.username eq "Alice" }, fetchUser)
        }
    }
    @BeforeEach
    fun clearDatabase(){
        database.deleteAll(OrderDishes)
        database.deleteAll(Orders)
        database.deleteAll(Reviews)
        database.deleteAll(Users)
        database.deleteAll(Dishes)
        database.deleteAll(Restaurants)
    }
}