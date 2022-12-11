package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.toList

class UserRepository(
    database: Database
) {
    private val db = database

    fun fetch(id: Int): User? {
        return db.users.find { it.id eq id }
    }

    fun delete(id: Int){
        db.delete(Users) { it.id eq id }
    }

    fun add(user: User): Int {

        db.users.add(user)
        return user.id

//        return db.insertAndGenerateKey(Users) {
//            set(it.username, user.username)
//            set(it.phone, user.phone)
//            set(it.email, user.email)
//            set(it.hashed_password, user.hashedPassword)
//            set(it.role_id, user.roleId)
//            set(it.restaurant_id, user.restaurant.id)
//        }.toString().toInt()
    }

    fun list() : List<User> {
        return db.users.toList()
    }
}
