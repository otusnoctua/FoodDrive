package ru.ac.uniyar.domain


class RolePermissionsRepository(
    rolePermissionsRepository: List<RolePermissions> = emptyList()
){
    private val repository = rolePermissionsRepository.associateBy { it.id }.toMutableMap()

    init {
        repository[0] = RolePermissions.ANONYMOUS_ROLE
        repository[1] = RolePermissions.CLIENT_ROLE
        repository[2] = RolePermissions.OPERATOR_ROLE
        repository[3] = RolePermissions.ADMIN_ROLE
    }

    fun fetch(id: Int): RolePermissions? = repository[id]

    fun add(rolePermissions: RolePermissions){
        if (repository.containsKey(rolePermissions.id)) return
        repository[rolePermissions.id] = rolePermissions
    }

    fun list() = repository.values.toList()
}