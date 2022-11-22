package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.ShowDishFormVM
import ru.ac.uniyar.models.ShowEditDishFormVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.*
import java.util.*

fun showDishForm(
    permissionLens: RequestContextLens<RolePermissions>,
    restaurantQueries: RestaurantQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@ { request ->
    val permissions = permissionLens(request)
    if (!permissions.createDish)
        Response (Status.UNAUTHORIZED)
    val idString = request.path("restaurant").orEmpty()
    val id = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQueries.FetchRestaurantViaId().invoke(id) ?: return@handler Response(Status.BAD_REQUEST)
    Response(Status.OK).with(htmlView(request) of ShowDishFormVM(restaurant = restaurant))
}

val dishNameFormLens = FormField.string().required("nameDish")
val veganFormLens = FormField.boolean().required("vegan")
val ingredientsFormLens = FormField.string().required("ingredients")
val descriptionFormLens = FormField.string().required("description")
val BodyDishFormLens = Body.webForm(
    Validator.Feedback, dishNameFormLens,
).toLens()

fun addDish(//сделать классом как в авторизации
    permissionLens: RequestContextLens<RolePermissions>,
    restaurantQueries: RestaurantQueries,
    dishQueries: DishQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@ { request ->
    val permissions = permissionLens(request)
    if (!permissions.createDish)
        Response (Status.UNAUTHORIZED)
    val id = UUID.fromString(request.path("restaurant").orEmpty()) ?: return@handler Response(Status.BAD_REQUEST)//<--пример
    val restaurant = restaurantQueries.FetchRestaurantViaId().invoke(id) ?: return@handler Response(Status.BAD_REQUEST)
    val webForm = BodyDishFormLens(request)
    if (webForm.errors.isEmpty()) {
        dishQueries.AddDishQuery().invoke(restaurant, dishNameFormLens(webForm), ingredientsFormLens(webForm), veganFormLens(webForm), descriptionFormLens(webForm))
        Response(Status.FOUND).header("Location", "/${restaurant.id}/ListOfDishes")//<--пример
    } else {
        Response(Status.OK).with(htmlView(request) of ShowDishFormVM(webForm, restaurant))
    }
}

fun editDish(
    permissionLens: RequestContextLens<RolePermissions>,
    dishQueries: DishQueries,
    restaurantQueries: RestaurantQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@ { request ->
    val idString = request.path("restaurant").orEmpty()
    val dishIdString = request.path("dish").orEmpty()
    val dishId = UUID.fromString(dishIdString) ?: return@handler Response(Status.BAD_REQUEST)
    val dish = dishQueries.FetchDishViaId().invoke(dishId) ?: return@handler Response(Status.BAD_REQUEST)
    val id = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQueries.FetchRestaurantViaId().invoke(id) ?: return@handler Response(Status.BAD_REQUEST)
    val permissions = permissionLens(request)
    if (!permissions.editDish)
        Response (Status.UNAUTHORIZED)
    val webForm = BodyDishFormLens(request)
    if (webForm.errors.isEmpty()) {
        dishQueries.EditDishQuery().invoke(dishNameFormLens(webForm), dish)
        Response(Status.FOUND).header("Location", "/"+idString+"/ListOfDishes")
    } else {
        Response(Status.OK).with(htmlView(request) of ShowEditDishFormVM(webForm,restaurant))
    }
}

fun showEditDishForm(
    permissionLens: RequestContextLens<RolePermissions>,
    restaurantQueries: RestaurantQueries,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@ { request ->
    val permissions = permissionLens(request)
    if (!permissions.editDish)
        Response (Status.UNAUTHORIZED)
    val idString = request.path("restaurant").orEmpty()
    val id = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQueries.FetchRestaurantViaId().invoke(id) ?: return@handler Response(Status.BAD_REQUEST)
    Response(Status.OK).with(htmlView(request) of ShowEditDishFormVM(restaurant = restaurant))
}