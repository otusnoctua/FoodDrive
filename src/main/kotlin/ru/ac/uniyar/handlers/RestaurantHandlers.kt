package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.path
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.domain.lensOrDefault
import ru.ac.uniyar.domain.lensOrNull
import ru.ac.uniyar.models.RestaurantFormVM
import ru.ac.uniyar.models.RestaurantVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.DishQueries
import ru.ac.uniyar.queries.RestaurantQueries
import ru.ac.uniyar.queries.ReviewQueries
import java.util.*

class RestaurantH(
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val dishQueries: DishQueries,
    private val restaurantQueries: RestaurantQueries,
    private val reviewQueries: ReviewQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    companion object {
        val dishNameLens= Query.string().optional("name")
        val sortByPriceLens=Query.int().defaulted("flag",-1)
        val minPriceLens=Query.int().optional("min")
        val maxPriceLens=Query.int().optional("max")
    }
    override fun invoke(request: Request): Response {
        val permissions = permissionsLens(request)
        if (!permissions.listDishes) {
            return Response(Status.UNAUTHORIZED)
        }
        val dishName= lensOrNull(dishNameLens,request)
        val flag= lensOrDefault(sortByPriceLens,request,-1)
        val minPrice= lensOrNull(minPriceLens,request)
        val maxPrice = lensOrNull(maxPriceLens,request)

        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(
            request.path("restaurant")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        ) ?: return Response(Status.BAD_REQUEST)
        return Response(Status.OK).with(
            htmlView(request) of RestaurantVM(
                dishQueries.FilterByNameQ().invoke(
                    dishName,restaurant.id,
                    flag,
                    minPrice,
                    maxPrice
                ),
                restaurant,
                name=dishName,
                flag=flag,
                reviewQueries.RatingForRestaurantQ().invoke(restaurant.id),
            )
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
        val restaurantNameFormLens = FormField.string().required("name")
        val logoUrlFormLens = FormField.string().required("logoUrl")
        val BodyRestaurantFormLens = Body.webForm(
            Validator.Feedback,
            restaurantNameFormLens,
            logoUrlFormLens
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.createRestaurant) {
            return Response(Status.UNAUTHORIZED)
        }
        val webForm = BodyRestaurantFormLens(request)
        return if (webForm.errors.isEmpty()) {
            restaurantQueries.AddRestaurantQ().invoke(restaurantNameFormLens(webForm), logoUrlFormLens(webForm))
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

class EditRestaurantH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    companion object {
        val restaurantNameFormLens = FormField.string().required("name")
        val BodyRestaurantFormLens = Body.webForm(
            Validator.Feedback,
            restaurantNameFormLens,
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.editRestaurant) {
            return Response(Status.UNAUTHORIZED)
        }
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(
            request.path("restaurant")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        )
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
        val restaurant = restaurantQueries.FetchRestaurantQ().invoke(
            request.path("restaurant")?.toIntOrNull() ?: return Response(Status.BAD_REQUEST)
        )
            ?: return Response(Status.BAD_REQUEST)
        val haveDishes = dishQueries.DishesOfRestaurantQ().invoke(restaurant.id).map { it.restaurant.id }.contains(restaurant.id)
        if (haveDishes) {
            return Response(Status.BAD_REQUEST)
        }
        restaurantQueries.DeleteRestaurantQ().invoke(restaurant.id)
        return Response(Status.FOUND).header(
            "Location", "/restaurants"
        )
    }
}