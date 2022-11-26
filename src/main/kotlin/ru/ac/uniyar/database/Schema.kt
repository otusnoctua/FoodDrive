package ru.ac.uniyar.database

import org.ktorm.schema.*

object Users : Table<Nothing>("users"){
    val id = int("id").primaryKey()
    val name = varchar("name")
    val phone = long("phone")
    val email = varchar("email")
    val password = text("password")
    val role_id = int("role_id")
}

object Restaurants : Table<Nothing>("restaurants"){
    val id = int("id").primaryKey()
    val name = varchar("name")
}

object Dishes : Table<Nothing>("dishes"){
    val id = int("id").primaryKey()
    val name = varchar("name")
    val restaurant_id = int("restaurant_id")
    val ingredients = text("ingredients")
    val vegan = boolean("vegan")
    val description = text("description")
}

object Orders : Table<Nothing>("orders"){
    val id = int("id").primaryKey()
    val client_id = int("client_id")
    val restaurant_id = int("restaurant_id")
    val status = varchar("status")
    val datetime = datetime("datetime")
}

object Reviews : Table<Nothing>("reviews"){
    val id = int("id").primaryKey()
    val user_id = int("user_id")
    val restaurant_id = int("restaurant_id")
    val text = text("text")
    val rating = int("rating")
    val datetime = datetime("datetime")
}

object OrderDish : Table<Nothing>("order_dish"){
    val id = int("id").primaryKey()
    val order_id = int("order_id")
    val dish_id = int("dish_id")
}

