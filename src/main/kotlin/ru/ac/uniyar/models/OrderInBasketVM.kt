package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Order

data class OrderInBasketVM(val order: Order):ViewModel
