package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.asJsonValue
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class Review(
    val id: UUID,
    val user_id: UUID,
    val restaurant_id: UUID,
    val text: String,
    val rating: Int,
    val timestamp: LocalDateTime,
    ){
    companion object{
        fun fromJson(node: JsonNode): Review {
            val jsonObject = node.asJsonObject()
            return Review(
                UUID.fromString(jsonObject["id"].asText()),
                UUID.fromString(jsonObject["user_id"].asText()),
                UUID.fromString(jsonObject["restaurant_id"].asText()),
                jsonObject["text"].asText(),
                jsonObject["rating"].asInt(),
                LocalDateTime.parse(jsonObject["timestamp"].asText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),

                )
        }
    }
    fun asJsonObject(): JsonNode {
        return listOf(
            "id" to id.toString().asJsonValue(),
            "user_id" to user_id.toString().asJsonValue(),
            "restaurant_id" to restaurant_id.toString().asJsonValue(),
            "text" to text.asJsonValue(),
            "rating" to rating.toString().asJsonValue(),
            "timestamp" to timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).asJsonValue(),
            ).asJsonObject()
    }

    fun setUuid(uuid: UUID): Review {
        return this.copy(id = uuid)
    }
}
