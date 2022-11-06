package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.asJsonValue
import java.util.*

data class Dish(
    val id: UUID,
    val restaurant_id : UUID,
    val ingredients: String,
    val vegan: Boolean,
    val description: String,


){
    companion object{
        fun fromJson(node: JsonNode): Dish {
            val jsonObject = node.asJsonObject()
            return Dish(
                UUID.fromString(jsonObject["id"].asText()),
                UUID.fromString(jsonObject["restaurant_id"].asText()),
                jsonObject["ingredients"].asText(),
                jsonObject["vegan"].asBoolean(),
                jsonObject["description"].asText(),


            )
        }
    }
    fun asJsonObject(): JsonNode{
        return listOf(
            "id" to id.toString().asJsonValue(),
            "restaurant_id" to restaurant_id.toString().asJsonValue(),
            "ingredients" to ingredients.asJsonValue(),
            "vegan" to vegan.asJsonValue(),
            "description" to description.asJsonValue(),

        ).asJsonObject()
    }

    fun setUuid(uuid: UUID): Dish {
        return this.copy(id = uuid)
    }
}
