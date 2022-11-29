package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import ru.ac.uniyar.queries.computations.hashPassword
import java.util.*

class UserQ (
    private val store: Store,
    private val settings: Settings,
    private val userRepository : UserRepository
) {
    inner class AddUserQ {
        operator fun invoke(name: String, phone: String, email: String, password: String): UUID {
            val hashedPassword = hashPassword(password, settings.salt)
            val roleId = UUID.fromString("26b9e5e7-1c8a-40e5-aece-2345f6b8afd9")
            val id = userRepository.add(
                User(
                    EMPTY_UUID,
                    name,
                    phone,
                    email,
                    hashedPassword,
                    roleId,
                )
            )
            store.save()
            return id;
        }
    }
    inner class FetchUserViaId {
        operator fun invoke(id: UUID): User? {
            return userRepository.fetch(id)
        }
    }
    inner class ListOfUsersQuery {
        operator fun invoke(): List<User> {
            return userRepository.list()
        }
    }
    inner class EditUserQuery {
        operator fun invoke(userName: String, userPhone: String, userEmail: String, user: User){
            userRepository.changeUser(userName, userPhone, userEmail, user)
            store.save()
        }
    }
}