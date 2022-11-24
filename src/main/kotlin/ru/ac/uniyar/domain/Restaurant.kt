package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.asJsonValue
import java.util.*

data class Restaurant(
    val id: Int,
    val nameRestaurant: String,
) {
    companion object {
        fun fromJson(node: JsonNode): Restaurant {
            val restaurant = node.asJsonObject()
            return Restaurant(
                restaurant["id"].asInt(),
                restaurant["nameRestaurant"].asText(),
            )
        }
    }
    fun asJsonObject(): JsonNode {
        return listOf(
            "id" to id.asJsonValue(),
            "nameRestaurant" to nameRestaurant.asJsonValue(),
        ).asJsonObject()
    }

}
