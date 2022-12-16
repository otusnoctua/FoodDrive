package ru.ac.uniyar

import org.http4k.core.*
import org.http4k.filter.ServerFilters
import org.http4k.lens.RequestContextKey
import org.http4k.lens.RequestContextLens
import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.deleteAll
import org.ktorm.dsl.eq
import ru.ac.uniyar.domain.*
import ru.ac.uniyar.domain.Store
import ru.ac.uniyar.handlers.HttpHandlerHolder
import ru.ac.uniyar.models.ErrorMessageVM
import ru.ac.uniyar.models.template.ContextAwarePebbleTemplates
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.*
import java.nio.file.Path

fun createApplication(): Http4kServer? {
    val database = Database.connect(
        url = "jdbc:mysql://127.0.0.1:3305/fooddrive",
        driver = "com.mysql.jdbc.Driver",
        user = "root",
        password = "12345"
    )


    val storeHolder = try {
        StoreHolder(Path.of("settings.json"),database)
    } catch (error: SettingsFileError) {
        println(error.message)
        return null

    }

    val renderer = ContextAwarePebbleTemplates().HotReload("src/main/resources")
    val htmlView = ContextAwareViewRender(renderer, ContentType.TEXT_HTML)

    val contexts = RequestContexts()
    val curUserLens: RequestContextLens<User?> = RequestContextKey.optional(contexts, name = "user")
    val permissionsLens: RequestContextLens<RolePermissions> =
        RequestContextKey.required(contexts, name = "permissions")
    val htmlViewWithContext = htmlView.associateContextLens("currentUser", curUserLens)
    val htmlViewPermissions = htmlView.associateContextLens("permissions", permissionsLens)

    val jwtTools = JwtTools(storeHolder.settings.salt, "ru.ac.uniyar.FoodDrive")

    val handlerHolder = HttpHandlerHolder(
        jwtTools,
        permissionsLens,
        curUserLens,
        htmlView,
        storeHolder,
    )

    fun showErrorMessageFilter(
        htmlView: ContextAwareViewRender,
    ): Filter = Filter { next: HttpHandler -> //external error check
        { request ->
            val response = next(request)
            if (response.status.successful) {
                response
            } else {
                response.with(htmlView(request) of ErrorMessageVM(request.uri))
            }
        }
    }

    val routingHttpHandler = static(ResourceLoader.Classpath("/ru/ac/uniyar/public/"))

    val router = Router(
        handlerHolder.pingHandler,
        handlerHolder.redirectToHomePage,

        handlerHolder.registerFormH,
        handlerHolder.registerH,
        handlerHolder.registerOperatorFormH,

        handlerHolder.loginFormH,
        handlerHolder.loginH,
        handlerHolder.logOutH,

        handlerHolder.profileH,
        handlerHolder.editProfileFormH,
        handlerHolder.editProfileH,

        handlerHolder.ordersH,
        handlerHolder.orderH,

        handlerHolder.homePageH,
        handlerHolder.addRestaurantFormH,
        handlerHolder.addRestaurantH,

        handlerHolder.editRestaurantH,
        handlerHolder.editRestaurantFormH,
        handlerHolder.deleteRestaurantH,

        handlerHolder.reviewsH,
        handlerHolder.reviewFormH,
        handlerHolder.addReviewH,

        handlerHolder.deleteDishH,
        handlerHolder.editAvailabilityH,
        handlerHolder.addDishToOrderH,

        handlerHolder.restaurantH,
        handlerHolder.addDishFormH,
        handlerHolder.addDishH,

        handlerHolder.editDishFormH,
        handlerHolder.editDishH,

        handlerHolder.basketH,
        handlerHolder.editStatusByUserH,
        handlerHolder.orderFromBasketH,
        handlerHolder.deleteOrderH,
        handlerHolder.deleteDishFromOrderH,

        handlerHolder.operatorOrdersH,
        handlerHolder.orderForOperatorH,
        handlerHolder.editStatusByOperatorH,

        routingHttpHandler,
    )
    val authorizedApp = authenticationFilter(
        curUserLens,
        storeHolder.fetchUserQ,
        jwtTools,
    ).then(
        authorizationFilter(
            curUserLens,
            permissionsLens,
            storeHolder.fetchPermissionsViaQuery,
        )
    ).then(
        router.invoke()
    )
    val staticFilesHandler = static(ResourceLoader.Classpath("/ru/ac/uniyar/public/"))
    val app = routes(
        authorizedApp,
        staticFilesHandler,
    )

    val printingApp: HttpHandler =
        ServerFilters.InitialiseRequestContext(contexts).then(showErrorMessageFilter(htmlView)).then(app)

    val server = printingApp.asServer(Undertow(5000))
    return server

    //  println("Server started on http://localhost:" + server.port())
}

fun fillingTables() {
    val database = Database.connect(
        url = "jdbc:mysql://127.0.0.1:3305/fooddrive",
        driver = "com.mysql.jdbc.Driver",
        user = "root",
        password = "12345"
    )
    val store = Store(database)
    val settings = Settings(Path.of("settings.json"))

    val userQueries = UserQueries(settings, store.userRepository)

    val reviewQueries = ReviewQueries(store.reviewRepository)
    val restaurantQueries = RestaurantQueries(store.restaurantRepository, reviewQueries)
    val dishQueries = DishQueries(store.dishRepository)
    val orderQueries = OrderQueries(store.orderRepository,database)
    val fetchUserQueries = FetchUserQ(store.userRepository)
    val fetchPermissionsQueries = FetchPermissionsQ(store.rolePermissionsRepository)
    val authenticateUserViaLoginQueries = AuthenticateUserViaLoginQ(settings, store.userRepository)


    database.useTransaction {
        database.deleteAll(OrderDishes)
        database.deleteAll(Orders)
        database.deleteAll(Reviews)
        database.deleteAll(Users)
        database.deleteAll(Dishes)
        database.deleteAll(Restaurants)

        userQueries.AddUserQ().invoke(
            "admin",123,"admin@examole.com","123",null
        )
        userQueries.AddUserQ().invoke(
            "Alice", 123, "alice@example.com", "123", null
        )
        val restaurantId = restaurantQueries.AddRestaurantQ().invoke(
            "TestRestaurant", "1"
        )
        dishQueries.AddDishQ().invoke(
            restaurantQueries.FetchRestaurantQ().invoke(restaurantId)!!,
            "TestDish",
            true,
            "something",
            "something",
            true,
            100,
            "1",
        )



    }
}

fun deletingTables() {
    val database = Database.connect(
        url = "jdbc:mysql://127.0.0.1:3305/fooddrive",
        driver = "com.mysql.jdbc.Driver",
        user = "root",
        password = "12345"
    )
    val store = Store(database)
    val settings = Settings(Path.of("settings.json"))

    val userQueries = UserQueries(settings, store.userRepository)

    val reviewQueries = ReviewQueries(store.reviewRepository)
    val restaurantQueries = RestaurantQueries(store.restaurantRepository, reviewQueries)
    val dishQueries = DishQueries(store.dishRepository)
    val orderQueries = OrderQueries(store.orderRepository,database)
    val fetchUserQueries = FetchUserQ(store.userRepository)
    val fetchPermissionsQueries = FetchPermissionsQ(store.rolePermissionsRepository)
    val authenticateUserViaLoginQueries = AuthenticateUserViaLoginQ(settings, store.userRepository)

    database.useTransaction {
        database.deleteAll(Users)
        database.deleteAll(Dishes)
        database.deleteAll(Restaurants)

    }

}