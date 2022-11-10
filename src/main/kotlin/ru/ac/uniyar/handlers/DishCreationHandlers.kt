package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.models.ShowDishFormVM
import ru.ac.uniyar.models.ShowEditDishFormVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.AddDishQuery
import ru.ac.uniyar.queries.DishQuery
import ru.ac.uniyar.queries.EditDishQuery
import ru.ac.uniyar.queries.RestaurantQuery
import java.util.*

fun showDishForm(
    permissionLens: RequestContextLens<RolePermissions>,
    restaurantQuery: RestaurantQuery,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@ { request ->
    val permissions = permissionLens(request)
    if (!permissions.createDish)
        Response (Status.UNAUTHORIZED)
    val idString = request.path("restaurant").orEmpty()
    val id = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQuery.invoke(id) ?: return@handler Response(Status.BAD_REQUEST)
    Response(Status.OK).with(htmlView(request) of ShowDishFormVM(restaurant = restaurant))
}

val dishNameFormLens = FormField.string().required("nameDish")
val veganFormLens = FormField.boolean().required("vegan")
val ingredientsFormLens = FormField.string().required("ingredients")
val descriptionFormLens = FormField.string().required("description")
val BodyDishFormLens = Body.webForm(
    Validator.Feedback, dishNameFormLens,
).toLens()

fun addDish(
    permissionLens: RequestContextLens<RolePermissions>,
    restaurantQuery: RestaurantQuery,
    addDishQuery: AddDishQuery,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@ { request ->
    val permissions = permissionLens(request)
    if (!permissions.createDish)
        Response (Status.UNAUTHORIZED)
    val idString = request.path("restaurant").orEmpty()
    val id = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQuery.invoke(id) ?: return@handler Response(Status.BAD_REQUEST)
    val webForm = BodyDishFormLens(request)
    if (webForm.errors.isEmpty()) {
        addDishQuery.invoke(restaurant, dishNameFormLens(webForm), ingredientsFormLens(webForm), veganFormLens(webForm), descriptionFormLens(webForm))
        Response(Status.FOUND).header("Location", "/"+idString+"/ListOfDishes")
    } else {
        Response(Status.OK).with(htmlView(request) of ShowDishFormVM(webForm, restaurant))
    }
}

fun editDish(
    permissionLens: RequestContextLens<RolePermissions>,
    dishQuery: DishQuery,
    restaurantQuery: RestaurantQuery,
    editDishQuery: EditDishQuery,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@ { request ->
    val idString = request.path("restaurant").orEmpty()
    val dishIdString = request.path("dish").orEmpty()
    val dishId = UUID.fromString(dishIdString) ?: return@handler Response(Status.BAD_REQUEST)
    val dish = dishQuery.invoke(dishId) ?: return@handler Response(Status.BAD_REQUEST)
    val id = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQuery.invoke(id) ?: return@handler Response(Status.BAD_REQUEST)
    val permissions = permissionLens(request)
    if (!permissions.editDish)
        Response (Status.UNAUTHORIZED)
    val webForm = BodyDishFormLens(request)
    if (webForm.errors.isEmpty()) {
        editDishQuery.invoke(dishNameFormLens(webForm), dish)
        Response(Status.FOUND).header("Location", "/"+idString+"/ListOfDishes")
    } else {
        Response(Status.OK).with(htmlView(request) of ShowEditDishFormVM(webForm,restaurant))
    }
}

fun showEditDishForm(
    permissionLens: RequestContextLens<RolePermissions>,
    restaurantQuery: RestaurantQuery,
    htmlView: ContextAwareViewRender,
): HttpHandler = handler@ { request ->
    val permissions = permissionLens(request)
    if (!permissions.editDish)
        Response (Status.UNAUTHORIZED)
    val idString = request.path("restaurant").orEmpty()
    val id = UUID.fromString(idString) ?: return@handler Response(Status.BAD_REQUEST)
    val restaurant = restaurantQuery.invoke(id) ?: return@handler Response(Status.BAD_REQUEST)
    Response(Status.OK).with(htmlView(request) of ShowEditDishFormVM(restaurant = restaurant))
}