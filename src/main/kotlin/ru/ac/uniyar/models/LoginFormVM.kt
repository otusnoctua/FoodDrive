package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.User

class LoginFormVM(
    val form: WebForm = WebForm(),
): ViewModel