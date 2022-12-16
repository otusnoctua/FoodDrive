package ru.ac.uniyar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.deleteAll
import org.ktorm.dsl.eq
import org.ktorm.entity.add
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

    val userQueries = UserQueries(settings, store.userRepository)
    val reviewQueries = ReviewQueries(store.reviewRepository)
    val restaurantQueries = RestaurantQueries(store.restaurantRepository, reviewQueries)
    val dishQueries = DishQueries(store.dishRepository)
    val orderQueries = OrderQueries(store.orderRepository,database)
    val fetchUserQueries = FetchUserQ(store.userRepository)
    val fetchPermissionsQueries = FetchPermissionsQ(store.rolePermissionsRepository)
    val authenticateUserViaLoginQueries = AuthenticateUserViaLoginQ(settings, store.userRepository)

    /** Проверяет добавление пользователя и соответствие роли клиента, если пользователю не назначен ресторан */
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
            assertEquals(database.users.find { it.username eq "Alice" }?.roleId, RolePermissions.ADMIN_ROLE.id)
            assertEquals(database.users.find { it.username eq "Alice" }?.email, "alice@example.com")
            assertEquals(database.users.find { it.username eq "Alice" }?.phone, 123)
        }

    }

    /** Проверка добавления ресторана*/
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

    /** Проверка добавления блюда*/
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

    /** Проверка добавления отзыва*/
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

    /** Проверка удаления блюда*/
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

    /** Проверка удаления ресторана*/
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

    /** Проверка редактирования ресторана (его имени)*/
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
                database.restaurants.find { it.restaurant_name eq "kfc" }!!.logoUrl,
                database.restaurants.find { it.restaurant_name eq "kfc" }!!)
            assertEquals(restaurantId, database.restaurants.find {it.restaurant_name eq "Burger King"}?.id)
            assertEquals(database.restaurants.find { it.id eq restaurantId }?.restaurantName, "Burger King")
        }
    }

    /** Проверка редактирования блюда (его имени, отметки "Вегетарианское", ингредиентов, описания, цены, ссылки на фото, отметки "В стоп-листе")*/
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

    /** Проверка редактирования пользователя (его имени, телефона, почты)*/
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

    /** Проверка удаления и добавления в стоп-лист*/
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
            dishQueries.AddDishQ().invoke(
                restaurantToAdd,
                "Салат",
                true,
                "Огурец, помидор",
                "2",
                false,
                30,
                "link2"
            )
            dishQueries.EditAvailability().invoke(
                database.dishes.find { it.dishName eq "Бургер" }!!
            )
            dishQueries.EditAvailability().invoke(
                database.dishes.find { it.dishName eq "Салат" }!!
            )
            assertEquals(database.dishes.find { it.dishName eq "Бургер" }?.availability, false)
            assertEquals(database.dishes.find { it.dishName eq "Салат" }?.availability, true)
        }
    }

    /** Проверка получения ресторана по id*/
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

    /** Проверка получения блюда по id*/
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

    /** Проверка получения пользователя по id*/
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

    /** Проверка получения списка блюд конкретного ресторана*/
    @Test
    fun dishesOfRestaurant(){
        database.useTransaction {
            restaurantQueries.AddRestaurantQ().invoke(
                "kfc",
                "link"
            )
            val restaurantKfc = database.restaurants.find { it.restaurant_name eq "kfc" }!!
            restaurantQueries.AddRestaurantQ().invoke(
                "Burger King",
                "link2"
            )
            val restaurantBurgerKing = database.restaurants.find { it.restaurant_name eq "Burger King" }!!
            dishQueries.AddDishQ().invoke(
                restaurantKfc,
                "Бургер",
                false,
                "Булка, курица",
                "1",
                true,
                100,
                "linkBurger"
            )
            dishQueries.AddDishQ().invoke(
                restaurantKfc,
                "Курица",
                false,
                "Курица",
                "2",
                true,
                150,
                "linkChicken"
            )
            dishQueries.AddDishQ().invoke(
                restaurantBurgerKing,
                "Креветки",
                false,
                "Креветки, масло",
                "5",
                true,
                200,
                "linkBK"
            )
            val dishOfKfc = mutableListOf(database.dishes.find { it.dishName eq "Бургер" }!!,
                database.dishes.find { it.dishName eq "Курица" }!!)
            val restaurantKfcId = database.restaurants.find { it.restaurant_name eq "kfc" }?.id!!
            val dishOfKfcQuery = dishQueries.DishesOfRestaurantQ().invoke(restaurantKfcId)
            assertEquals(dishOfKfc, dishOfKfcQuery )
        }
    }

    /** Проверка получения списка ресторанов*/
    @Test
    fun listOfRestaurants(){
        database.useTransaction {
            restaurantQueries.AddRestaurantQ().invoke(
                "kfc",
                "link1"
            )
            val restaurantKfc = database.restaurants.find { it.restaurant_name eq "kfc" }!!
            restaurantQueries.AddRestaurantQ().invoke(
                "Burger King",
                "link2"
            )
            val restaurantBurgerKing = database.restaurants.find { it.restaurant_name eq "Burger King" }!!
            restaurantQueries.AddRestaurantQ().invoke(
                "Bazar",
                "link3"
            )
            val restaurantBazar = database.restaurants.find { it.restaurant_name eq "Bazar" }!!
            val listOfRestaurants = mutableListOf(restaurantKfc, restaurantBurgerKing, restaurantBazar)
            val listOfRestaurantsQuery = restaurantQueries.RestaurantsQ().invoke()
            assertEquals(listOfRestaurants, listOfRestaurantsQuery)
        }
    }

    /** Добавление нового заказа и добавление блюда в заказ */
    @Test
    fun createOrderWithDishes(){

        val cafe = Restaurant {
            restaurantName = "cafe"
            logoUrl = "logo_here"
        }

        database.restaurants.add(cafe)
        val cafeFromDB = database.restaurants.find { it.restaurant_name eq "cafe" }!!

        dishQueries.AddDishQ().invoke(
            cafeFromDB,
            "salad",
            true,
            "vegetables",
            "salad",
            true,
            100,
            "link here"
        )
        val saladFromDB = database.dishes.find { it.dishName eq "salad" }

        dishQueries.AddDishQ().invoke(
            cafeFromDB,
            "soup",
            false,
            "vegetables and meat",
            "yummy",
            true,
            160,
            "link here"
        )
        val soupFromDB = database.dishes.find {it.dishName eq "soup"}

        userQueries.AddUserQ().invoke(
            "Alice",
            123,
            "alice@example.com",
            "hashed_password_here",
            null,
        )

        val clientFromDB = database.users.find { it.username eq "Alice" }!!

        val order = Order {
            client = clientFromDB
            restaurant = cafeFromDB
            orderStatus = "В ожидании"
            startTime = LocalDateTime.now()
            orderCheck = 0
        }

        val orderDish1 = OrderDish {
            orderId = database.orders.find { it.id eq orderQueries.AddOrderQ().invoke(order).id }!!.id
            dishId = saladFromDB!!.id
        }
        database.order_dishes.add(orderDish1)

        val orderFromDB = database.orders.find { it.client_id eq clientFromDB.id }

        assertNotEquals(orderFromDB, null)

        val orderDishesFromDB = orderQueries.FetchOrderDishes().invoke(orderFromDB!!)

        assertNotEquals(orderDishesFromDB.find { it.dishName == "salad" }, null)

        //добавить заказ
        //получить dish из order
        //проверить orders и ordersDish
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