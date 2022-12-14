package ru.ac.uniyar

import org.http4k.core.*
import org.http4k.filter.ServerFilters
import org.http4k.lens.RequestContextKey
import org.http4k.lens.RequestContextLens
import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.TemplateRenderer
import org.ktorm.database.Database
import ru.ac.uniyar.domain.*
import ru.ac.uniyar.handlers.HttpHandlerHolder
import ru.ac.uniyar.models.DishFormVM
import ru.ac.uniyar.models.ErrorMessageVM
import ru.ac.uniyar.models.template.ContextAwarePebbleTemplates
import ru.ac.uniyar.models.template.ContextAwareTemplateRenderer
import ru.ac.uniyar.models.template.ContextAwareViewRender
import java.nio.file.Path


fun main() {

    val database = Database.connect(
        url = "jdbc:mysql://127.0.0.1:3306/fooddrive",
        driver = "com.mysql.jdbc.Driver",
        user = "root",
        password = "12345"
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
    val curUserLens: RequestContextLens<User?> = RequestContextKey.optional(contexts, name = "user")
    val permissionsLens: RequestContextLens<RolePermissions> =
        RequestContextKey.required(contexts, name = "permissions")
    val htmlViewWithContext = htmlView.associateContextLens("currentUser", curUserLens)
    val htmlViewPermissions= htmlView.associateContextLens("permissions", permissionsLens)

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
        {request ->
            val response = next(request)
            if (response.status.successful){
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
    val app = routes (
        authorizedApp,
        staticFilesHandler,
    )

    val printingApp: HttpHandler =
        ServerFilters.InitialiseRequestContext(contexts)
            .then(showErrorMessageFilter(htmlView))
            .then(app)

    val server = printingApp.asServer(Undertow(9000)).start()
    println("Server started on http://localhost:" + server.port())
}
