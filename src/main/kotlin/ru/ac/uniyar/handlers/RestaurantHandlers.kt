package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.RestaurantFormVM
import ru.ac.uniyar.models.RestaurantVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.DishQueries
import ru.ac.uniyar.queries.RestaurantQueries
import java.util.*

class RestaurantH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val dishQueries: DishQueries,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.listDishes)
            return Response(Status.UNAUTHORIZED)
        val id = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val restaurant =
            restaurantQueries.FetchRestaurantQ().invoke(id) ?: return Response(Status.BAD_REQUEST)
        val model = RestaurantVM(dishQueries.DishesOfRestaurantQ().invoke(restaurant.id), restaurant)
        return Response(Status.OK).with(htmlView(request) of model)
    }
}

class AddRestaurantFormH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.createRestaurant)
            return Response(Status.UNAUTHORIZED)
        return Response(Status.OK).with(htmlView(request) of RestaurantFormVM(isEdit = false))
    }
}

class AddRestaurantH(
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
            restaurantQueries.AddRestaurantQ().invoke(restaurantNameFormLens(webForm))
            return Response(Status.FOUND).header("Location", "/restaurants")
        } else {
            return Response(Status.OK).with(htmlView(request) of RestaurantFormVM(webForm,isEdit = false))
        }
    }
}


class EditRestaurantH(
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
        val id = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val restaurant =
            restaurantQueries.FetchRestaurantQ().invoke(id) ?: return Response(Status.BAD_REQUEST)
        val permissions = permissionLens(request)
        if (!permissions.editRestaurant)
            return Response(Status.UNAUTHORIZED)
        val webForm = BodyRestaurantFormLens(request)
        if (webForm.errors.isEmpty()) {
            restaurantQueries.EditRestaurantQ().invoke(restaurantNameFormLens(webForm), restaurant)
            return Response(Status.FOUND).header("Location", "/restaurants")
        } else {
            return Response(Status.OK).with(htmlView(request) of RestaurantFormVM(webForm,isEdit = true))
        }
    }
}
class EditRestaurantFormH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.editRestaurant)
            return Response(Status.UNAUTHORIZED)
        return Response(Status.OK).with(htmlView(request) of RestaurantFormVM(isEdit = true ))
    }
}

class DeleteRestaurantH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val dishQueries: DishQueries,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val restaurantId = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val restaurant =
            restaurantQueries.FetchRestaurantQ().invoke(restaurantId) ?: return Response(Status.BAD_REQUEST)
        val permissionsDelete = permissionLens(request)
        val haveDishes =
            dishQueries.DishesOfRestaurantQ().invoke(restaurant.id).map { it.restaurantId }.contains(restaurant.id)
        if (haveDishes)
            return Response(Status.BAD_REQUEST)
        if (!permissionsDelete.deleteRestaurant)
            return Response(Status.UNAUTHORIZED)
        val permissions = permissionLens(request)
        if (!permissions.listRestaurants)
            return Response(Status.UNAUTHORIZED)
        restaurantQueries.DeleteRestaurantQ().invoke(restaurantId)
        return Response(Status.FOUND).header("Location", "/restaurants")
    }
}