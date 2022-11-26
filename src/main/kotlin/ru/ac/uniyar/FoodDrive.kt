package ru.ac.uniyar

import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.RequestContexts
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.lens.RequestContextKey
import org.http4k.lens.RequestContextLens
import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.ktorm.database.Database
import ru.ac.uniyar.domain.*
import ru.ac.uniyar.handlers.HttpHandlerHolder
import ru.ac.uniyar.models.template.ContextAwarePebbleTemplates
import ru.ac.uniyar.models.template.ContextAwareViewRender
import java.nio.file.Path

fun main() {

    val database = Database.connect(
        url = "jdbc:mysql://127.0.0.1:3306/fooddrive",
        driver = "com.mysql.jdbc.Driver",
        user = "fda",
        password = "poorsara"
    )

    val storeHolder = try {
        StoreHolder(Path.of("settings.json"), database)
    }catch (error: SettingsFileError) {
        println(error.message)
        return
    }

    val renderer = ContextAwarePebbleTemplates().HotReload("src/main/resources")
    val htmlView = ContextAwareViewRender(renderer, ContentType.TEXT_HTML)

    val contexts = RequestContexts()
    val currentUserLens: RequestContextLens<User?> = RequestContextKey.optional(contexts, name = "user")
    val permissionsLens: RequestContextLens<RolePermissions> =
        RequestContextKey.required(contexts, name = "permissions")
    val htmlViewWithContext = htmlView.associateContextLens("currentUser", currentUserLens)
    val htmlViewPermissions = htmlView.associateContextLens("permissions", permissionsLens)

    val jwtTools = JwtTools(storeHolder.settings.salt, "ru.ac.uniyar.AnimalsList2")

    val handlerHolder = HttpHandlerHolder(
        jwtTools,
        currentUserLens,
        permissionsLens,
        htmlView,
        storeHolder,
    )
    val routingHttpHandler = static(ResourceLoader.Classpath("/ru/ac/uniyar/public/"))

    val router = Router(
        handlerHolder.pingHandler,
        handlerHolder.redirectToRestaurants,
        handlerHolder.showListOfRestaurants,
        handlerHolder.showListOfDishes,
        handlerHolder.showUserForm,
        handlerHolder.addUser,
        handlerHolder.showLoginFormHandler,
        handlerHolder.authenticateUser,
        handlerHolder.logOutUser,
        handlerHolder.showRestaurantForm,
        handlerHolder.addRestaurant,
        handlerHolder.deleteRestaurant,
        handlerHolder.editRestaurant,
        handlerHolder.showEditRestaurantForm,
        handlerHolder.deleteDish,
        handlerHolder.showDishForm,
        handlerHolder.addDish,
        handlerHolder.showEditDishForm,
        handlerHolder.editDish,
        handlerHolder.showBasket,
        handlerHolder.addDishToOrder,
        handlerHolder.showOrder,
        handlerHolder.deleteOrder,
        handlerHolder.deleteDishFromOrder,
        handlerHolder.editStatusByUser,
        handlerHolder.showReviewList,
        handlerHolder.showReviewForm,
        handlerHolder.addReviewToList,

        routingHttpHandler,
    )
    val authorizedApp = authenticationFilter(
        currentUserLens,
        storeHolder.fetchUserViaUserId,
        jwtTools,
    ).then(
        authorizationFilter(
            currentUserLens,
            permissionsLens,
            storeHolder.fetchPermissionsViaQuery,
        )
    ).then(
        router.invoke()
    )
    val staticFilesHandler = static(ResourceLoader.Classpath("/ru/ac/uniyar/public/"))
    val app = routes (
        authorizedApp,
        staticFilesHandler,
    )

    val printingApp: HttpHandler =
        ServerFilters.InitialiseRequestContext(contexts)
            .then(app)

    val server = printingApp.asServer(Undertow(9000)).start()
    println("Server started on http://localhost:" + server.port())
}
