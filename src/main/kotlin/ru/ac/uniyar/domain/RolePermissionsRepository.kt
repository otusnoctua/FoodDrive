package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonArray
import java.util.*

class RolePermissionsRepository(rolePermissionsRepository: List<RolePermissions> = emptyList()
){
    private val repository = rolePermissionsRepository.associateBy { it.id }.toMutableMap()

    companion object{
        fun fromJson(node: JsonNode): RolePermissionsRepository {
            val array = node.asJsonArray()
            return RolePermissionsRepository(array.map { RolePermissions.fromJson(it) })
        }
    }

    fun asJsonObject(): JsonNode = repository.values
        .map { it.asJsonObject() }
        .asJsonArray()

    fun fetch(id: UUID): RolePermissions? = repository[id]

    fun add(rolePermissions: RolePermissions){
        if (repository.containsKey(rolePermissions.id)) return
        repository[rolePermissions.id] = rolePermissions
    }

    fun list() = repository.values.toList()
}