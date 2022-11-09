package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.asJsonValue
import java.util.*

data class Restaurant(
    val id: UUID,
    val nameRestaurant: String,
) {
    companion object {
        fun fromJson(node: JsonNode): Restaurant {
            val restaurant = node.asJsonObject()
            return Restaurant(
                UUID.fromString(restaurant["id"].asText()),
                restaurant["nameRestaurant"].asText(),
            )
        }
    }
    fun asJsonObject(): JsonNode {
        return listOf(
            "id" to id.toString().asJsonValue(),
            "nameRestaurant" to nameRestaurant.asJsonValue(),
        ).asJsonObject()
    }

    fun setUuid(uuid: UUID): Restaurant {
        return this.copy(id = uuid)
    }
    fun setRestaurantName(nameRestaurant: String, uuid: UUID): Restaurant {
        return this.copy(nameRestaurant = nameRestaurant, id = uuid)
    }

}
