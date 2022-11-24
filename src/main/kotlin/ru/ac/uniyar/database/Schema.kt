package ru.ac.uniyar.database

import org.ktorm.schema.*

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

object Restaurants : Table<Nothing>("restaurants"){
    val id = int("id").primaryKey()
    val name = varchar("name")
}

object Reviews : Table<Nothing>("reviews"){
    val id = int("id").primaryKey()
    val user_id = int("user_id")
    val restaurant_id = int("restaurant_id")
    val text = text("text")
    val rating = int("rating")
    val datetime = datetime("datetime")
}

object Roles : Table<Nothing>("roles"){
    val id = int("id").primaryKey()
    val name = varchar("name")
    val list_orders = boolean("list_orders")
    val list_users = boolean("list_users")
    val list_restaurants = boolean("list_restaurants")
    val list_dishes = boolean("list_dishes")
    val list_reviews = boolean("list_reviews")
    val create_order = boolean("create_order")
    val create_user = boolean("create_user")
    val create_restaurant = boolean("create_restaurant")
    val create_dish = boolean("create_dish")
    val create_review = boolean("create_review")
    val view_order = boolean("view_order")
    val view_user = boolean("view_user")
    val view_restaurant = boolean("view_restaurant")
    val view_dish = boolean("view_dish")
    val view_review = boolean("view_review")
    val edit_order = boolean("edit_order")
    val edit_user = boolean("edit_user")
    val edit_restaurant = boolean("edit_restaurant")
    val edit_dish = boolean("edit_dish")
    val edit_review = boolean("edit_review")
    val delete_order = boolean("delete_order")
    val delete_user = boolean("delete_user")
    val delete_restaurant = boolean("delete_restaurant")
    val delete_dish = boolean("delete_dish")
    val delete_review = boolean("delete_review")
}

object Users : Table<Nothing>("users"){
    val id = int("id").primaryKey()
    val name = varchar("name")
    val phone = int("phone")
    val email = varchar("email")
    val password = text("password")
    val role_id = int("role_id")
}

