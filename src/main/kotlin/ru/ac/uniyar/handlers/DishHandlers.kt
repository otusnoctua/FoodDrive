package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.DishFormVM
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
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(
            request.path("restaurant")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(
            htmlView(request) of DishFormVM(
                restaurant = restaurant,
                isEdit = false,
            )
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
        private val dishNameFormLens = FormField.string().required("name")
        private val veganFormLens = FormField.boolean().required("vegan")
        private val ingredientsFormLens = FormField.string().required("ingredients")
        private val descriptionFormLens = FormField.string().required("description")
        private val availabilityFormLens = FormField.boolean().required("availability")
        private val priceFormLens = FormField.int().required("price")
        private val imageUrlFormLens = FormField.string().required("imageUrl")

        private val BodyDishFormLens = Body.webForm(
            Validator.Feedback,
            dishNameFormLens,
            veganFormLens,
            ingredientsFormLens,
            descriptionFormLens,
            availabilityFormLens,
            priceFormLens,
            imageUrlFormLens
        ).toLens()
    }

    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.createDish) {
            Response(Status.UNAUTHORIZED)
        }
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(
            request.path("restaurant")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        )
            ?: return Response(Status.BAD_REQUEST)
        val webForm = BodyDishFormLens(request)
        return if (webForm.errors.isEmpty()) {
            dishQueries.AddDishQ().invoke(
                restaurant,
                dishNameFormLens(webForm),
                veganFormLens(webForm),
                ingredientsFormLens(webForm),
                descriptionFormLens(webForm),
                availabilityFormLens(webForm),
                priceFormLens(webForm),
                imageUrlFormLens(webForm)
                )
            Response(Status.FOUND).header(
                "Location", "/${restaurant.id}/ListOfDishes"
            )
        } else {
            Response(Status.OK).with(
                htmlView(request) of DishFormVM(
                    webForm,
                    restaurant,
                    isEdit=false,
                )
            )
        }
    }
}

class EditDishFormH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val dishQueries: DishQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    companion object {
        private val dishNameFormLens = FormField.string().required("name")
        private val veganFormLens = FormField.boolean().required("vegan")
        private val ingredientsFormLens = FormField.string().required("ingredients")
        private val descriptionFormLens = FormField.string().required("description")
        private val availabilityFormLens = FormField.boolean().required("availability")
        private val priceFormLens = FormField.int().required("price")
        private val imageUrlFormLens = FormField.string().required("imageUrl")
        private val BodyDishFormLens = Body.webForm(
            Validator.Feedback,
            dishNameFormLens,
            veganFormLens,
            ingredientsFormLens,
            descriptionFormLens,
            availabilityFormLens,
            priceFormLens,
            imageUrlFormLens
        ).toLens()
    }

    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.editDish) {
            return Response(Status.UNAUTHORIZED)
        }
        val dish = dishQueries.FetchDishQ().invoke(
            request.path("dish")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(
            request.path("restaurant")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val webForm = BodyDishFormLens(request)

        return Response(Status.OK).with(
            htmlView(request) of DishFormVM(
                restaurant = restaurant,
                isEdit = true,
            )
        )
    }
}

class EditDishH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val dishQueries: DishQueries,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler{
    companion object {
        private val dishNameFormLens = FormField.string().required("name")
        private val veganFormLens = FormField.boolean().required("vegan")
        private val ingredientsFormLens = FormField.string().required("ingredients")
        private val descriptionFormLens = FormField.string().required("description")
        private val availabilityFormLens = FormField.boolean().required("availability")
        private val priceFormLens = FormField.int().required("price")
        private val imageUrlFormLens = FormField.string().required("imageUrl")
        private val BodyDishFormLens = Body.webForm(
            Validator.Feedback,
            dishNameFormLens,
            veganFormLens,
            ingredientsFormLens,
            descriptionFormLens,
            availabilityFormLens,
            priceFormLens,
            imageUrlFormLens
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.editDish) {
            Response(Status.UNAUTHORIZED)
        }
        val dish = dishQueries.FetchDishQ().invoke(
            request.path("dish")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(
            request.path("restaurant")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val webForm = BodyDishFormLens(request)
        return if (webForm.errors.isEmpty()) {
            dishQueries.EditDishQ().invoke(
                dishNameFormLens(webForm),
                ingredientsFormLens(webForm),
                priceFormLens(webForm),
                descriptionFormLens(webForm),
                veganFormLens(webForm),
                availabilityFormLens(webForm),
                imageUrlFormLens(webForm),
                dish
            )
            Response(Status.FOUND).header("Location", "/${restaurant.id}/ListOfDishes")
        } else {
            Response(Status.OK).with(
                htmlView(request) of DishFormVM(
                    webForm,
                    restaurant,
                    isEdit = true)
            )
        }
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
        val dish = dishQueries.FetchDishQ().invoke(
            request.path("dish")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(
            request.path("restaurant")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
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
        val dish = dishQueries.FetchDishQ().invoke(
            request.path("dish")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(
            request.path("restaurant")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        dishQueries.EditAvailability().invoke(dish)
        return Response(Status.FOUND).header(
            "Location", "/${restaurant.id}/ListOfDishes"
        )
    }
}
