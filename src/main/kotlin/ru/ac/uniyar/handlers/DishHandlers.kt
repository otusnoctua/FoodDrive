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
        if (!permissions.createDish)
            return Response(Status.UNAUTHORIZED)
        val id = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val restaurant =
            restaurantQueries.FetchRestaurantQ().invoke(id) ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(htmlView(request) of DishFormVM(restaurant = restaurant, isEdit = false))
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
        private val BodyDishFormLens = Body.webForm(
            Validator.Feedback, dishNameFormLens,
        ).toLens()
    }

    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.createDish)
            Response(Status.UNAUTHORIZED)
        val id = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(id) ?: return Response(Status.BAD_REQUEST)
        val webForm = BodyDishFormLens(request)
        if (webForm.errors.isEmpty()) {
            dishQueries.AddDishQ().invoke(
                restaurant,
                dishNameFormLens(webForm),
                ingredientsFormLens(webForm),
                veganFormLens(webForm),
                descriptionFormLens(webForm),

            )
            return Response(Status.FOUND).header("Location", "/${restaurant.id}/ListOfDishes")//<--пример
        } else {
            return Response(Status.OK).with(htmlView(request) of DishFormVM(webForm, restaurant,isEdit=false))
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
        private val BodyDishFormLens = Body.webForm(
            Validator.Feedback, dishNameFormLens,
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        val dishId = UUID.fromString(request.path("dish").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val dish = dishQueries.FetchDishQ().invoke(dishId) ?: return Response(Status.BAD_REQUEST)
        val id = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(id) ?: return Response(Status.BAD_REQUEST)
        val permissions = permissionLens(request)
        if (!permissions.editDish)
            Response(Status.UNAUTHORIZED)
        val webForm = BodyDishFormLens(request)
        if (webForm.errors.isEmpty()) {
            dishQueries.EditDishQ().invoke(dishNameFormLens(webForm), dish)
            return Response(Status.FOUND).header("Location", "/${restaurant.id}/ListOfDishes")
        } else {
            return Response(Status.OK).with(htmlView(request) of DishFormVM(webForm, restaurant,isEdit=true))
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
        if (!permissions.editDish)
            return Response(Status.UNAUTHORIZED)
        val id = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val restaurant =
            restaurantQueries.FetchRestaurantQ().invoke(id) ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(htmlView(request) of DishFormVM(restaurant = restaurant, isEdit = true))
    }
}

class DeleteDishH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val dishQueries: DishQueries,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val dishId = UUID.fromString(request.path("dish").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val dish = dishQueries.FetchDishQ().invoke(dishId) ?: return Response(Status.BAD_REQUEST)
        val restaurantId = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val permissionsDelete = permissionLens(request)
        if (!permissionsDelete.deleteDish)
            return Response(Status.UNAUTHORIZED)
        val permissions = permissionLens(request)
        if (!permissions.listDishes)
            return Response(Status.UNAUTHORIZED)
        dishQueries.DeleteDishQ().invoke(dish)
        return Response(Status.FOUND).header("Location", "/${restaurantId}/ListOfDishes")
    }
}

class EditAvailabilityH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val dishQueries: DishQueries,
): HttpHandler{
    override fun invoke(request: Request): Response {
        val dishId = UUID.fromString(request.path("dish").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        val dish = dishQueries.FetchDishQ().invoke(dishId) ?: return Response(Status.BAD_REQUEST)
        val restaurantId = UUID.fromString(request.path("restaurant").orEmpty()) ?: return Response(Status.BAD_REQUEST)
        if (!permissionLens(request).editStopList)
            return Response(Status.UNAUTHORIZED)
        if (!permissionLens(request).listDishes)
            return Response(Status.UNAUTHORIZED)
        dishQueries.EditAvailability().invoke(dish)
        return Response(Status.FOUND).header("Location", "/${restaurantId}/ListOfDishes")
    }
}
