package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import org.http4k.lens.*
import ru.ac.uniyar.domain.*
import ru.ac.uniyar.models.RegisterOperatorVM
import ru.ac.uniyar.models.RegisterVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.RestaurantQueries
import ru.ac.uniyar.queries.UserQueries

class RegisterFormH(
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        return Response(OK).with(
            htmlView(request) of RegisterVM()
        )
    }
}

class RegisterOperatorFormH(
    private val htmlView: ContextAwareViewRender,
    private val restaurantQueries: RestaurantQueries,
):HttpHandler{
    override fun invoke(request: Request): Response {
        return Response(OK).with(
            htmlView(request) of RegisterOperatorVM(restaurantQueries.RestaurantsQ().invoke())
        )
    }
}

class RegisterH(
    private val userQueries: UserQueries,
    private val permissionsLens: RequestContextLens<RolePermissions>,
    private val restaurantRepository: RestaurantRepository,
    private val restaurantQueries: RestaurantQueries,
    private val htmlView: ContextAwareViewRender,
    private val jwtTools: JwtTools,
): HttpHandler {
    companion object {
        val userNameFormLens = FormField.nonEmptyString().required("name")
        val userPhoneFormLens = FormField.long().required("phone")
        val userEmailFormLens = FormField.nonEmptyString().required("email")
        val passwordOneFormLens = FormField.nonEmptyString().required("passwordOne")
        val passwordTwoFormLens = FormField.nonEmptyString().required("passwordTwo")
        val linkToRestaurantFormLens= FormField.int().defaulted("restaurant", -1)
        val BodyUserFormLens = Body.webForm(
            Validator.Feedback,
            userNameFormLens,
            userPhoneFormLens,
            userEmailFormLens,
            passwordOneFormLens,
            passwordTwoFormLens,
            linkToRestaurantFormLens,
        ).toLens()
    }

    override fun invoke(request: Request): Response {
        var form = BodyUserFormLens(request)
        val firstPassword = lensOrNull(passwordOneFormLens, form)
        val secondPassword = lensOrNull(passwordTwoFormLens, form)
        if (firstPassword != null && firstPassword != secondPassword) {
            val newError = form.errors + Invalid(passwordOneFormLens.meta.copy(description = "password must match"))
            form = form.copy(errors = newError)
        }
        val restaurant = restaurantRepository.fetch(linkToRestaurantFormLens(form))
        if (form.errors.isEmpty()) {
            val userId = userQueries.AddUserQ().invoke(
                userNameFormLens(form),
                userPhoneFormLens(form),
                userEmailFormLens(form),
                firstPassword!!,
                restaurant,
            )

            if (linkToRestaurantFormLens(form) != -1 ){
                return Response(FOUND)
                    .header("Location", "/")
            }
            else {
                val token = jwtTools.create(userId.toString()) ?: return Response(Status.INTERNAL_SERVER_ERROR)
                val authCookie = Cookie("token", token, httpOnly = true, sameSite = SameSite.Strict)
                return Response(FOUND)
                    .header("Location", "/")
                    .cookie(authCookie)
            }
        }
        else {
            return if (permissionsLens.invoke(request).createOperator) {
                Response(Status.OK).with(
                    htmlView(request) of RegisterOperatorVM(
                        restaurantQueries.RestaurantsQ().invoke(),
                        form = form,
                    )
                )
            } else {
                Response(BAD_REQUEST).with(
                    htmlView(request) of RegisterVM()
                )
            }
        }
    }
}

