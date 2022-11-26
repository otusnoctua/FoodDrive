package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import ru.ac.uniyar.queries.computations.hashPassword
import java.util.*

class AddUserQuery (
    private val store: Store,
    private val settings: Settings,
    private val userRepository : UserRepository
) {
    operator fun invoke(name: String, phone: Long, email: String, password: String): Int {
        val hashedPassword = hashPassword(password, settings.salt)
        return userRepository.add(
            User(
                0,
                name,
                phone,
                email,
                hashedPassword,
                1,
            )
        )
    }
}