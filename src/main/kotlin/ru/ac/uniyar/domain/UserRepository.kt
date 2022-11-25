package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonArray
import org.ktorm.database.Database
import org.ktorm.dsl.*
import ru.ac.uniyar.database.Restaurants
import ru.ac.uniyar.database.Users
import java.util.*

class UserRepository(
    database: Database
) {
    private val db = database

    fun fetch(id: Int): User {
        return db
            .from(Users)
            .select()
            .where{Users.id.toInt() eq id }
            .map { User(
                it.getInt(1),
                it.getString(2)!!,
                it.getLong(3),
                it.getString(4)!!,
                it.getString(5)!!,
                it.getInt(6)
            ) }.first()
    }

    fun delete(id: Int){
        db.delete(Users) { it.id eq id }
    }

    fun add(user: User): Int {
        return db.insertAndGenerateKey(Users) {
            set(it.name, user.name)
            set(it.phone, user.phone)
            set(it.email, user.email)
            set(it.password, user.password)
            set(it.role_id, user.roleId)
        }.toString().toInt()
    }

    fun list() : List<User> {
        return db.from(Users).select().map {
            User(
                it.getInt(1),
                it.getString(2)!!,
                it.getLong(3),
                it.getString(4)!!,
                it.getString(5)!!,
                it.getInt(6)
            )
        }
    }

}
