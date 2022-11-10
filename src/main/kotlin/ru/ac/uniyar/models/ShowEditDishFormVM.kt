package ru.ac.uniyar.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Restaurant

class ShowEditDishFormVM (val form: WebForm = WebForm(), val restaurant: Restaurant): ViewModel