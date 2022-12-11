package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.User
import ru.ac.uniyar.domain.UserRepository

class FetchUserViaUserId (private val userRepository: UserRepository) {

    operator fun invoke(userId: String): User? {
        return userRepository.fetch(userId.toInt())
    }
}