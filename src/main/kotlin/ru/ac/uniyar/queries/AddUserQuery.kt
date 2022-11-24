package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import ru.ac.uniyar.queries.computations.hashPassword
import java.util.*

class AddUserQuery (
    private val store: Store,
    private val settings: Settings,
    private val userRepository : UserRepository
) {
    operator fun invoke(name: String, phone: String, email: String, password: String): UUID {
        val hashedPassword = hashPassword(password, settings.salt)
        return userRepository.add(
            User(
                EMPTY_UUID,
                name,
                phone,
                email,
                hashedPassword,
                1,
            )
        )
        store.save()
    }
}