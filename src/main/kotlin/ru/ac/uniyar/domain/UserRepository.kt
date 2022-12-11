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
    }

    fun list() : List<User> {
        return db.users.toList()
    }

    fun changeUser(userName: String, userPhone: Long, userEmail: String, user: User) {
        val userToEdit = db.users.find { it.id eq user.id }
        userToEdit?.username = userName
        userToEdit?.phone = userPhone
        userToEdit?.email = userEmail
        userToEdit?.flushChanges()
    }
}
