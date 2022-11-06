package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonArray
import java.util.*

class ReviewRepository(review: Iterable<Review> = emptyList()) {
    private val reviews = review.associateBy { it.id }.toMutableMap()

    companion object{
        fun fromJson(node: JsonNode) : ReviewRepository {
            val reviews = node.map{
                Review.fromJson(it)
            }
            return ReviewRepository(reviews)
        }
    }

    fun asJsonObject(): JsonNode {
        return reviews.values
            .map{ it.asJsonObject() }
            .asJsonArray()
    }

    fun fetch(id: UUID): Review? = reviews[id]

    fun add(review: Review): UUID {
        var newId = review.id
        while (reviews.containsKey(newId) || newId == EMPTY_UUID){
            newId = UUID.randomUUID()
        }
        reviews[newId] = review.setUuid(newId)
        return newId
    }

    fun delete(id: UUID) {
        reviews.remove(id)
    }

    fun edit(review: Review){
        reviews[review.id] = review
    }

    fun list() = reviews.values.toList()
}