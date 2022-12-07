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
import ru.ac.uniyar.domain.*
import ru.ac.uniyar.handlers.HttpHandlerHolder
import ru.ac.uniyar.models.template.ContextAwarePebbleTemplates
import ru.ac.uniyar.models.template.ContextAwareViewRender
import java.nio.file.Path


fun main() {
    val storeHolder = try {
        StoreHolder(Path.of("storage.json"), Path.of("settings.json"))
    }catch (error: SettingsFileError) {
        println(error.message)
        return
    }
    Runtime.getRuntime().addShutdownHook(storeHolder.store.storeThread)

    val renderer = ContextAwarePebbleTemplates().HotReload("src/main/resources")
    val htmlView = ContextAwareViewRender(renderer, ContentType.TEXT_HTML)

    val contexts = RequestContexts()
    val curUserLens: RequestContextLens<User?> = RequestContextKey.optional(contexts, name = "user")
    val permissionsLens: RequestContextLens<RolePermissions> =
        RequestContextKey.required(contexts, name = "permissions")
    val htmlViewWithContext = htmlView.associateContextLens("currentUser", curUserLens)
    val htmlViewPermissions= htmlView.associateContextLens("permissions", permissionsLens)

    val jwtTools = JwtTools(storeHolder.settings.salt, "ru.ac.uniyar.AnimalsList2")

    val handlerHolder = HttpHandlerHolder(
        jwtTools,
        permissionsLens,
        curUserLens,
        htmlView,
        storeHolder,
    )
    val routingHttpHandler = static(ResourceLoader.Classpath("/ru/ac/uniyar/public/"))

    val router = Router(
        handlerHolder.pingHandler,
        handlerHolder.redirectToHomePage,
        handlerHolder.homePageH,
        handlerHolder.restaurantH,
        handlerHolder.registerFormH,
        handlerHolder.registerH,
        handlerHolder.loginFormH,
        handlerHolder.loginH,
        handlerHolder.logOutH,
        handlerHolder.addRestaurantFormH,
        handlerHolder.addRestaurantH,
        handlerHolder.deleteRestaurantH,
        handlerHolder.editRestaurantH,
        handlerHolder.editRestaurantFormH,
        handlerHolder.deleteDishH,
        handlerHolder.addDishFormH,
        handlerHolder.addDishH,
        handlerHolder.editDishFormH,
        handlerHolder.editDishH,
        handlerHolder.basketH,
        handlerHolder.addDishToOrderH,
        handlerHolder.deleteOrderH,
        handlerHolder.deleteDishFromOrderH,
        handlerHolder.editStatusByUserH,
        handlerHolder.reviewsH,
        handlerHolder.reviewFormH,
        handlerHolder.addReviewH,
        handlerHolder.profileH,
        handlerHolder.editProfileFormH,
        handlerHolder.editProfileH,
        handlerHolder.ordersH,
        handlerHolder.orderH,
        handlerHolder.orderFromBasketH,

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
