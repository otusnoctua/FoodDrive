package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import ru.ac.uniyar.queries.computations.hashPassword

class AddUserQuery (
    private val store: Store,
    private val settings: Settings,
    private val userRepository : UserRepository
) {
    operator fun invoke(
        userName: String,
        userPhone: Long,
        userEmail: String,
        userPassword: String,
        userRestaurant: Restaurant
    ): Int {
        val userHashedPassword = hashPassword(userPassword, settings.salt)
        val user = User {
            username = userName
            phone = userPhone
            email = userEmail
            hashedPassword = userHashedPassword
            roleId = 1
            restaurant = userRestaurant
        }

        return userRepository.add(user)
    }
}