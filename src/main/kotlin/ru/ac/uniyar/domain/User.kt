package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*

interface User : Entity<User> {
    companion object : Entity.Factory<User>()
    val id: Int
    var username: String
    var phone: Long
    var email: String
    var hashedPassword: String
    var roleId: Int
    var restaurant: Restaurant
}

object Users : Table<User>("users"){
    val id = int("id").primaryKey().bindTo { it.id }
    val username = varchar("username").bindTo { it.username }
    val phone = long("phone").bindTo { it.phone }
    val email = varchar("email").bindTo { it.email }
    val hashed_password = text("hashed_password").bindTo { it.hashedPassword }
    val role_id = int("role_id")
    val restaurant_id = int("restaurant_id").references(Restaurants) { it.restaurant }
}

val Database.users get() = this.sequenceOf(Users)