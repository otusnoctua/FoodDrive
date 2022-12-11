package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*


interface Dish : Entity<Dish> {
    companion object : Entity.Factory<Dish>()
    val id: Int
    var dishName: String
    var restaurant : Restaurant
    var ingredients: String
    var vegan: Boolean
    var dishDescription: String
    var availability: Boolean
    var price: Int
    var imageUrl: String
}

object Dishes : Table<Dish>("dishes"){
    val id = int("id").primaryKey().bindTo { it.id }
    val dishName = varchar("dish_name").bindTo { it.dishName }
    val restaurant_id = int("restaurant_id").references(Restaurants) {it.restaurant}
    val ingredients = text("ingredients").bindTo { it.ingredients }
    val vegan = boolean("vegan").bindTo { it.vegan }
    val dishDescription = text("dish_description").bindTo { it.dishDescription }
    val availability = boolean("availability").bindTo { it.availability }
    val price = int("price").bindTo { it.price }
    val imageUrl = text("image_url").bindTo { it.imageUrl }
}

val Database.dishes get() = this.sequenceOf(Dishes)

