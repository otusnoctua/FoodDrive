package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Restaurant
import ru.ac.uniyar.domain.RestaurantInfo

data class RegisterVM (
    val form: WebForm = WebForm(),
    ): ViewModel