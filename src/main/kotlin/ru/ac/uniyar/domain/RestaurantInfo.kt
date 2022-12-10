package ru.ac.uniyar.domain

import java.util.DoubleSummaryStatistics

data class RestaurantInfo(
    val restaurant: Restaurant,
    val haveDish: Boolean,
    val rating: Double,
)