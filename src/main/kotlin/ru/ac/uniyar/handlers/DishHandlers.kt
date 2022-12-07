package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.DishFormVM
import ru.ac.uniyar.models.RestaurantFormVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.*
import java.util.*

class AddDishFormH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.createDish) {
            return Response(Status.UNAUTHORIZED)
        }
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(UUID.fromString(request.path("restaurant").orEmpty()))
                ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(
            htmlView(request) of DishFormVM(restaurant = restaurant, isEdit = false)
        )
    }
}

class AddDishH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val dishQueries: DishQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    companion object {
        private val dishNameFormLens = FormField.string().required("nameDish")
        private val veganFormLens = FormField.boolean().required("vegan")
        private val ingredientsFormLens = FormField.string().required("ingredients")
        private val descriptionFormLens = FormField.string().required("description")
        private val priceFormLens = FormField.int().required("price")
        private val BodyDishFormLens = Body.webForm(
            Validator.Feedback, dishNameFormLens,
        ).toLens()
    }

    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.createDish) {
            Response(Status.UNAUTHORIZED)
        }
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(UUID.fromString(request.path("restaurant").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val webForm = BodyDishFormLens(request)
        return if (webForm.errors.isEmpty()) {
            dishQueries.AddDishQ().invoke(
                restaurant,
                dishNameFormLens(webForm),
                ingredientsFormLens(webForm),
                priceFormLens(webForm),
                veganFormLens(webForm),
                descriptionFormLens(webForm),
                )
            Response(Status.FOUND).header(
                "Location", "/${restaurant.id}/ListOfDishes"
            )
        } else {
            Response(Status.OK).with(
                htmlView(request) of DishFormVM(webForm, restaurant, isEdit=false)
            )
        }
    }
}

class EditDishH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val dishQueries: DishQueries,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler{
    companion object {
        private val dishNameFormLens = FormField.string().required("nameDish")
        private val veganFormLens = FormField.boolean().required("vegan")
        private val ingredientsFormLens = FormField.string().required("ingredients")
        private val descriptionFormLens = FormField.string().required("description")
        private val priceFormLens = FormField.int().required("price")
        private val BodyDishFormLens = Body.webForm(
            Validator.Feedback, dishNameFormLens,
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.editDish) {
            Response(Status.UNAUTHORIZED)
        }
        val dish = dishQueries.FetchDishQ().invoke(UUID.fromString(request.path("dish").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(UUID.fromString(request.path("restaurant").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val webForm = BodyDishFormLens(request)
        return if (webForm.errors.isEmpty()) {
            dishQueries.EditDishQ().invoke(
                dishNameFormLens(webForm),
                ingredientsFormLens(webForm),
                priceFormLens(webForm),
                descriptionFormLens(webForm),
                veganFormLens(webForm),
                dish
            )
            Response(Status.FOUND).header("Location", "/${restaurant.id}/ListOfDishes")
        } else {
            Response(Status.OK).with(
                htmlView(request) of DishFormVM(webForm, restaurant, isEdit = true)
            )
        }
    }
}

class EditDishFormH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.editDish) {
            return Response(Status.UNAUTHORIZED)
        }
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(UUID.fromString(request.path("restaurant").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(
            htmlView(request) of DishFormVM(restaurant = restaurant, isEdit = true)
        )
    }
}

class DeleteDishH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val dishQueries: DishQueries,
    private val restaurantQueries: RestaurantQueries,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.deleteDish || permissions.listDishes) {
            return Response(Status.UNAUTHORIZED)
        }
        val dish = dishQueries.FetchDishQ().invoke(UUID.fromString(request.path("dish").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(UUID.fromString(request.path("restaurant").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        dishQueries.DeleteDishQ().invoke(dish)
        return Response(Status.FOUND).header(
            "Location", "/${restaurant.id}/ListOfDishes"
        )
    }
}

class EditAvailabilityH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val dishQueries: DishQueries,
    private val restaurantQueries: RestaurantQueries,
): HttpHandler{
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.editStopList || permissions.listDishes) {
            return Response(Status.UNAUTHORIZED)
        }
        val dish = dishQueries.FetchDishQ().invoke(UUID.fromString(request.path("dish").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(UUID.fromString(request.path("restaurant").orEmpty()))
            ?: return Response(Status.BAD_REQUEST)
        dishQueries.EditAvailability().invoke(dish)
        return Response(Status.FOUND).header(
            "Location", "/${restaurant.id}/ListOfDishes"
        )
    }
}
