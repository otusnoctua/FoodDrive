package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

class LoginVM(
    val form: WebForm = WebForm(),
): ViewModel