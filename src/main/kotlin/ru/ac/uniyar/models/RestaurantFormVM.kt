package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

data class RestaurantFormVM(
    val form: WebForm = WebForm(),
    val isEdit: Boolean,
    ): ViewModel