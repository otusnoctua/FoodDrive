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
        if (!permissions.listDishes) {
            return Response(Status.UNAUTHORIZED)
        }
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(UUID.fromString(request.path("restaurant").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(
            htmlView(request) of RestaurantVM(dishQueries.DishesOfRestaurantQ().invoke(restaurant.id), restaurant)
        )
    }
}

class AddRestaurantFormH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.createRestaurant) {
            return Response(Status.UNAUTHORIZED)
        }
        return Response(Status.OK).with(
            htmlView(request) of RestaurantFormVM(isEdit = false)
        )
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
        if (!permissions.createRestaurant) {
            return Response(Status.UNAUTHORIZED)
        }
        val webForm = BodyRestaurantFormLens(request)
        return if (webForm.errors.isEmpty()) {
            restaurantQueries.AddRestaurantQ().invoke(restaurantNameFormLens(webForm))
            Response(Status.FOUND).header(
                "Location", "/restaurants"
            )
        } else {
            Response(Status.OK).with(
                htmlView(request) of RestaurantFormVM(webForm,isEdit = false)
            )
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
        val permissions = permissionLens(request)
        if (!permissions.editRestaurant) {
            return Response(Status.UNAUTHORIZED)
        }
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(UUID.fromString(request.path("restaurant").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val webForm = BodyRestaurantFormLens(request)
        return if (webForm.errors.isEmpty()) {
            restaurantQueries.EditRestaurantQ().invoke(restaurantNameFormLens(webForm), restaurant)
            Response(Status.FOUND).header(
                "Location", "/restaurants"
            )
        } else {
            Response(Status.OK).with(
                htmlView(request) of RestaurantFormVM(webForm,isEdit = true)
            )
        }
    }
}
class EditRestaurantFormH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.editRestaurant) {
            return Response(Status.UNAUTHORIZED)
        }
        return Response(Status.OK).with(
            htmlView(request) of RestaurantFormVM(isEdit = true )
        )
    }
}

class DeleteRestaurantH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val dishQueries: DishQueries,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.deleteRestaurant || !permissions.listRestaurants) {
            return Response(Status.UNAUTHORIZED)
        }
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(UUID.fromString(request.path("restaurant").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val haveDishes = dishQueries.DishesOfRestaurantQ().invoke(restaurant.id).map { it.restaurantId }.contains(restaurant.id)
        if (haveDishes) {
            return Response(Status.BAD_REQUEST)
        }
        restaurantQueries.DeleteRestaurantQ().invoke(restaurant.id)
        return Response(Status.FOUND).header(
            "Location", "/restaurants"
        )
    }
}