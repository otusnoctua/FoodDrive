package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.ShowEditRestaurantFormVM
import ru.ac.uniyar.models.ShowRestaurantFormVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.RestaurantQueries
import java.util.*

class ShowRestaurantForm(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.createRestaurant)
            return Response(Status.UNAUTHORIZED)
        return Response(Status.OK).with(htmlView(request) of ShowRestaurantFormVM())
    }
}

class AddRestaurant(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    companion object {
        val restaurantNameFormLens = FormField.string().required("nameRestaurant")
        val BodyRestaurantFormLens = Body.webForm(
            Validator.Feedback, restaurantNameFormLens,
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.createRestaurant)
            return Response(Status.UNAUTHORIZED)
        val webForm = BodyRestaurantFormLens(request)
        if (webForm.errors.isEmpty()) {
            restaurantQueries.AddRestaurantQuery().invoke(restaurantNameFormLens(webForm))
            return Response(Status.FOUND).header("Location", "/restaurants")
        } else {
            return Response(Status.OK).with(htmlView(request) of ShowRestaurantFormVM(webForm))
        }
    }
}


class EditRestaurant(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    companion object {
        val restaurantNameFormLens = FormField.string().required("nameRestaurant")
        val BodyRestaurantFormLens = Body.webForm(
            Validator.Feedback, restaurantNameFormLens,
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        val id = request.path("restaurant")!!.toInt()
        val restaurant =
            restaurantQueries.FetchRestaurantViaId().invoke(id)
        val permissions = permissionLens(request)
        if (!permissions.editRestaurant)
            return Response(Status.UNAUTHORIZED)
        val webForm = BodyRestaurantFormLens(request)
        if (webForm.errors.isEmpty()) {
            restaurantQueries.EditRestaurantQuery().invoke(restaurantNameFormLens(webForm), restaurant)
            return Response(Status.FOUND).header("Location", "/restaurants")
        } else {
            return Response(Status.OK).with(htmlView(request) of ShowEditRestaurantFormVM(webForm))
        }
    }
}
class ShowEditRestaurantForm(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.editRestaurant)
            return Response(Status.UNAUTHORIZED)
        return Response(Status.OK).with(htmlView(request) of ShowEditRestaurantFormVM())
    }
}