package ru.ac.uniyar.handlers

import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.*
import ru.ac.uniyar.domain.lensOrNull
import ru.ac.uniyar.models.RegisterVM
import ru.ac.uniyar.models.template.ContextAwareViewRender
import ru.ac.uniyar.queries.AddUserQ


class RegisterFormH(
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    override fun invoke(request: Request): Response {
        return Response(OK).with(htmlView(request) of RegisterVM())
    }
}


class RegisterH(
    private val addUserQ: AddUserQ,
    private val htmlView: ContextAwareViewRender,
): HttpHandler {
    companion object {
        val userNameFormLens = FormField.nonEmptyString().required("name")
        val userPhoneFormLens = FormField.nonEmptyString().required("phone")
        val userEmailFormLens = FormField.nonEmptyString().required("email")
        val passwordOneFormLens = FormField.nonEmptyString().required("passwordOne")
        val passwordTwoFormLens = FormField.nonEmptyString().required("passwordTwo")
        val BodyUserFormLens = Body.webForm(
            Validator.Feedback, userNameFormLens, userPhoneFormLens, userEmailFormLens, passwordOneFormLens, passwordTwoFormLens,
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
        if (form.errors.isEmpty()) {
            addUserQ.invoke(
                userNameFormLens(form),
                userPhoneFormLens(form),
                userEmailFormLens(form),
                firstPassword!!,
            )
            return Response(FOUND).header("Location", "/")
        }
        return Response(BAD_REQUEST).with(htmlView(request) of RegisterVM(form))
    }
}