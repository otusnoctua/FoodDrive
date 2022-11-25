package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.asJsonValue
import java.util.*

data class User(
    val id: Int,
    val name: String,
    val phone: Long,
    val email: String,
    val password: String,
    val roleId: Int,

    ){
    companion object{
        fun fromJson(node: JsonNode): User {
            val user = node.asJsonObject()
            return User(
                user["id"].asInt(),
                user["name"].asText(),
                user["phone"].asLong(),
                user["email"].asText(),
                user["password"].asText(),
                user["roleId"].asInt(),

            )
        }
    }

    fun asJsonObject(): JsonNode {
        return listOf(
            "id" to id.toString().asJsonValue(),
            "name" to name.asJsonValue(),
            "phone" to phone.asJsonValue(),
            "email" to email.asJsonValue(),
            "password" to password.asJsonValue(),
            "roleId" to roleId.asJsonValue(),

        ).asJsonObject()
    }
}
