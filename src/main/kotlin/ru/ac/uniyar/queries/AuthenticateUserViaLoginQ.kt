package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Settings
import ru.ac.uniyar.domain.UserRepository
import ru.ac.uniyar.queries.computations.hashPassword
import java.lang.RuntimeException

class AuthenticateUserViaLoginQ(
    private val settings: Settings,
    private val usersRepository: UserRepository
) {

    operator fun invoke(name: String, password: String): String {
        val user = usersRepository.list().find { it.username == name} ?: throw AuthenticationError()
        val hashedPassword = hashPassword(password, settings.salt)
        if (hashedPassword != user.hashedPassword)
            throw  AuthenticationError()
        return user.id.toString()
    }
}
class AuthenticationError: RuntimeException("User or password is wrong")