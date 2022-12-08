package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

data class LoginVM(
    val form: WebForm = WebForm(),
    ): ViewModel