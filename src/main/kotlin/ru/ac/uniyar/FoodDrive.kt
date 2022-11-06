package ru.ac.uniyar

import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Undertow
import org.http4k.server.asServer
import ru.ac.uniyar.domain.SettingsFileError
import ru.ac.uniyar.domain.StoreHolder
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

    val handlerHolder = HttpHandlerHolder(
        htmlView,
        storeHolder,
    )
    val routingHttpHandler = static(ResourceLoader.Classpath("/ru/ac/uniyar/public/"))

    val router = Router(
        handlerHolder.pingHandler,
        handlerHolder.redirectToRestaurants,
        handlerHolder.showListOfRestaurants,
        handlerHolder.showListOfDishes,

        routingHttpHandler,
    )
    val staticFilesHandler = static(ResourceLoader.Classpath("/ru/ac/uniyar/public/"))
    val app = routes (
        router.invoke(),
        staticFilesHandler,
    )

    val printingApp: HttpHandler =
        PrintRequest()
            .then(app)

    val server = printingApp.asServer(Undertow(9000)).start()
    println("Server started on http://localhost:" + server.port())
}
