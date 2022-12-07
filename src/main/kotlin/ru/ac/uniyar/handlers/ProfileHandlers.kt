package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.lens.*
import ru.ac.uniyar.domain.RolePermissions
import ru.ac.uniyar.domain.User
import ru.ac.uniyar.models.ProfileFormVM
import ru.ac.uniyar.models.ProfileVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.UserQueries

class ProfileH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val htmlView: ContextAwareViewRender,
):HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.viewUser)
            return Response(Status.UNAUTHORIZED)
        return Response(Status.OK).with(htmlView(request) of ProfileVM())
    }
}

class EditProfileFormH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        val permissions = permissionLens(request)
        if (!permissions.editUser)
            return Response(Status.UNAUTHORIZED)
        return Response(Status.OK).with(htmlView(request) of ProfileFormVM())
    }
}

class EditProfileH(
    private val permissionLens: RequestContextLens<RolePermissions>,
    private val curUserLens: RequestContextLens<User?>,
    private val userQueries: UserQueries,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    companion object {
        val userNameFormLens = FormField.nonEmptyString().required("name")
        val userPhoneFormLens = FormField.nonEmptyString().required("phone")
        val userEmailFormLens = FormField.nonEmptyString().required("email")
        val BodyUserFormLens = Body.webForm(
            Validator.Feedback, userNameFormLens, userPhoneFormLens, userEmailFormLens,
        ).toLens()
    }
    override fun invoke(request: Request): Response {
        val user = curUserLens(request)
        val permissions = permissionLens(request)
        if (!permissions.editUser || user == null)
            return Response(Status.UNAUTHORIZED)
        val webForm = BodyUserFormLens(request)
        if (webForm.errors.isEmpty()) {
            userQueries.EditUserQuery().invoke(userNameFormLens(webForm), userPhoneFormLens(webForm), userEmailFormLens(webForm), user)
            return Response(Status.FOUND).header("Location", "/profile")
        } else {
            return Response(Status.OK).with(htmlView(request) of ProfileFormVM(webForm))
        }
    }
}