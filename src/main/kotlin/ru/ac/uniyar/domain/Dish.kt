package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.asJsonValue
import java.util.*

data class Dish(
    val id: UUID,
    val restaurantId : UUID,
    val ingredients: String,
    val price: Int,
    val vegan: Boolean,
    val description: String,
    val nameDish: String,
    val availability:Boolean,
    ){

    companion object{
        fun fromJson(node: JsonNode): Dish {
            val jsonObject = node.asJsonObject()
            return Dish(
                UUID.fromString(jsonObject["id"].asText()),
                UUID.fromString(jsonObject["restaurantId"].asText()),
                jsonObject["ingredients"].asText(),
                jsonObject["price"].asInt(),
                jsonObject["vegan"].asBoolean(),
                jsonObject["description"].asText(),
                jsonObject["nameDish"].asText(),
                jsonObject["availability"].asBoolean(),
            )
        }
    }

    fun asJsonObject(): JsonNode{
        return listOf(
            "id" to id.toString().asJsonValue(),
            "restaurantId" to restaurantId.toString().asJsonValue(),
            "ingredients" to ingredients.asJsonValue(),
            "price" to price.asJsonValue(),
            "vegan" to vegan.asJsonValue(),
            "description" to description.asJsonValue(),
            "nameDish" to nameDish.asJsonValue(),

            "availability" to availability.asJsonValue(),
        ).asJsonObject()
    }

    fun setUuid(uuid: UUID): Dish {
        return this.copy(id = uuid)
    }

    fun editAvailability():Dish{
        return this.copy(availability = !availability)
    }
}
