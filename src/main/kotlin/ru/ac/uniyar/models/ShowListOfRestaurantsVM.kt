package ru.ac.uniyar.models

import org.http4k.template.ViewModel
import ru.ac.uniyar.domain.Restaurant

class ShowListOfRestaurantsVM(val listOfRestaurants: List<Restaurant>): ViewModel