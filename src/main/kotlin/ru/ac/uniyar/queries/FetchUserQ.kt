package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.User
import ru.ac.uniyar.domain.UserRepository
import java.lang.IllegalArgumentException
import java.util.*

class FetchUserQ (private val userRepository: UserRepository) {
    operator fun invoke(userId: String): User? {
        val uuid = try {
            UUID.fromString(userId)
        } catch (_: IllegalArgumentException) {
            return null
        }
        return userRepository.fetch(uuid)
    }
}