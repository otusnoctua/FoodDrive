package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*

interface Restaurant : Entity<Restaurant> {
    companion object : Entity.Factory<Restaurant>()
    val id: Int
    var restaurantName: String
    var logoUrl: String
}

object Restaurants : Table<Restaurant>("restaurants"){
    val id = int("id").primaryKey().bindTo { it.id }
    val restaurant_name = varchar("restaurant_name").bindTo { it.restaurantName }
    val logo_url = text("logo_url").bindTo { it.logoUrl }
}

val Database.restaurants get() = this.sequenceOf(Restaurants)
