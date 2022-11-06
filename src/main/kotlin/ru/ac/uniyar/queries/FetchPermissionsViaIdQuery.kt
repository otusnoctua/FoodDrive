package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.RolePermissionsRepository
import java.util.*

class FetchPermissionsViaIdQuery(private val permissionsRepository: RolePermissionsRepository) {
    operator fun invoke(roleId: UUID) = permissionsRepository.fetch(roleId)
}