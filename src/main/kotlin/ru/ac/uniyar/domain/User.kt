package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.asJsonValue
import java.util.*

data class User(
    val id: UUID,
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val roleId: UUID,
    val restaurantId:UUID,

    ){
    companion object{
        fun fromJson(node: JsonNode): User {
            val user = node.asJsonObject()
            return User(
                UUID.fromString(user["id"].asText()),
                user["name"].asText(),
                user["phone"].asText(),
                user["email"].asText(),
                user["password"].asText(),
                UUID.fromString(user["roleId"].asText()),
                UUID.fromString(user["linkToRestaurant"].asText()),
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
            "roleId" to roleId.toString().asJsonValue(),
            "linkToRestaurant" to restaurantId.toString().asJsonValue(),
        ).asJsonObject()
    }

    fun setUuid(uuid: UUID): User {
        return this.copy(id = uuid)
    }
}
