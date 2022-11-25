package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.User
import ru.ac.uniyar.domain.UserRepository
import java.lang.IllegalArgumentException
import java.util.*

class FetchUserViaUserId (private val userRepository: UserRepository) {

    operator fun invoke(userId: Int): User {
        return userRepository.fetch(userId)
    }
}