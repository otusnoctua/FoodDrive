package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonArray
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.asJsonValue
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class Order(
    val id: UUID,
    val clientId: Int,
    val restaurantId: Int,
    val status: String,
    val timestamp:LocalDateTime,
    val dishes: List<UUID>,
){
    companion object{
        fun fromJson(node: JsonNode): Order {
            val jsonObject = node.asJsonObject()
            return Order(
                UUID.fromString(jsonObject["id"].asText()),
                jsonObject["clientId"].asInt(),
                jsonObject["restaurantId"].asInt(),
                jsonObject["status"].asText(),
                LocalDateTime.parse(jsonObject["timestamp"].asText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                jsonObject["dishes"].asJsonArray().map { UUID.fromString(it.asText()) },

                )
        }
    }
    fun asJsonObject(): JsonNode {
        return listOf(
            "id" to id.toString().asJsonValue(),
            "clientId" to clientId.toString().asJsonValue(),
            "restaurantId" to restaurantId.toString().asJsonValue(),
            "status" to status.asJsonValue(),
            "timestamp" to timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).asJsonValue(),
            "dishes" to dishes.asJsonObject(),
        ).asJsonObject()
    }

    fun setUuid(uuid: UUID): Order {
        return this.copy(id = uuid)
    }
    fun addElementToDishes(nameDish: String):Order{
        val mas= this.dishes.toMutableList()
        mas.add(UUID.fromString(nameDish))
        return this.copy(dishes = mas)
    }
    fun deleteElementFromDishes(index:Int):Order{
        val mas= this.dishes.toMutableList()
        mas.removeAt(index)
        return this.copy(dishes = mas)
    }

    fun editStatus(status: String):Order{
        return this.copy(status = status)
    }

}
