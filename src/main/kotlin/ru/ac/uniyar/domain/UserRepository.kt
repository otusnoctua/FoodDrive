package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonArray
import java.util.*

class UserRepository(user: Iterable<User> = emptyList()) {
    private val allUsers = user.associateBy { it.id }.toMutableMap()

    companion object{

        fun fromJson(node: JsonNode) : UserRepository {
            val allUsers = node.map{
                User.fromJson(it)
            }
            return UserRepository(allUsers)
        }
    }

    fun asJsonObject(): JsonNode {
        return allUsers.values
            .map{ it.asJsonObject() }
            .asJsonArray()
    }

    fun fetch(id: UUID): User? = allUsers[id]

    fun add(user: User): UUID {
        var newId = user.id
        while (allUsers.containsKey(newId) || newId == EMPTY_UUID){
            newId = UUID.randomUUID()
        }
        allUsers[newId] = user.setUuid(newId)
        return newId
    }

    fun list() = allUsers.values.toList()

}
