package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

data class ProfileFormVM (
    val form: WebForm = WebForm(),
    ): ViewModel