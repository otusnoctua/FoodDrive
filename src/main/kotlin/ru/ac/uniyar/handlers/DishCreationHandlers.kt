package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.ShowDishFormVM
import ru.ac.uniyar.models.ShowEditDishFormVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.*

class ShowDishForm(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.createDish)
            return Response(Status.UNAUTHORIZED)
        val id = request.path("restaurant")!!.toInt()
        val restaurant =
            restaurantQueries.FetchRestaurantViaId().invoke(id) ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(htmlView(request) of ShowDishFormVM(restaurant = restaurant))
    }
}

class AddDish(
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
        private val availabilityFormLens = FormField.boolean().required("availability")
        private val priceFormLens = FormField.int().required("price")
        private val imageUrlFormLens = FormField.string().required("imageUrl")
        private val BodyDishFormLens = Body.webForm(
            Validator.Feedback, dishNameFormLens,
        ).toLens()
    }

    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.createDish)
            Response(Status.UNAUTHORIZED)
        val id = request.path("restaurant")!!.toInt()
        val restaurant = restaurantQueries.FetchRestaurantViaId().invoke(id) ?: return Response(Status.BAD_REQUEST)
        val webForm = BodyDishFormLens(request)
        if (webForm.errors.isEmpty()) {
            dishQueries.AddDishQuery().invoke(
                restaurant,
                dishNameFormLens(webForm),
                ingredientsFormLens(webForm),
                veganFormLens(webForm),
                descriptionFormLens(webForm),
                availabilityFormLens(webForm),
                priceFormLens(webForm),
                imageUrlFormLens(webForm)
            )
            return Response(Status.FOUND).header("Location", "/${restaurant.id}/ListOfDishes")//<--пример
        } else {
            return Response(Status.OK).with(htmlView(request) of ShowDishFormVM(webForm, restaurant))
        }
    }
}

class EditDish(
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
        private val availabilityFormLens = FormField.boolean().required("availability")
        private val priceFormLens = FormField.int().required("price")
        private val imageUrlFormLens = FormField.string().required("imageUrl")
        private val BodyDishFormLens = Body.webForm(
            Validator.Feedback, dishNameFormLens,
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        val dishId = request.path("dish")!!.toInt()
        val dish = dishQueries.FetchDishViaId().invoke(dishId) ?: return Response(Status.BAD_REQUEST)
        val id = request.path("restaurant")!!.toInt()
        val restaurant = restaurantQueries.FetchRestaurantViaId().invoke(id) ?: return Response(Status.BAD_REQUEST)
        val permissions = permissionLens(request)
        if (!permissions.editDish)
            Response(Status.UNAUTHORIZED)
        val webForm = BodyDishFormLens(request)
        if (webForm.errors.isEmpty()) {
            dishQueries.EditDishQuery().invoke(
                dishNameFormLens(webForm),
                ingredientsFormLens(webForm),
                veganFormLens(webForm),
                descriptionFormLens(webForm),
                availabilityFormLens(webForm),
                priceFormLens(webForm),
                imageUrlFormLens(webForm),
                dish)
            return Response(Status.FOUND).header("Location", "/${restaurant.id}/ListOfDishes")
        } else {
            return Response(Status.OK).with(htmlView(request) of ShowEditDishFormVM(webForm, restaurant))
        }
    }
}

class ShowEditDishForm(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.editDish)
            return Response(Status.UNAUTHORIZED)
        val id = request.path("restaurant")!!.toInt()
        val restaurant = restaurantQueries.FetchRestaurantViaId().invoke(id)
        return if (restaurant != null) {
            Response(Status.OK).with(htmlView(request) of ShowEditDishFormVM(restaurant = restaurant))
        } else {
            Response(Status.NOT_FOUND)
        }
    }
}