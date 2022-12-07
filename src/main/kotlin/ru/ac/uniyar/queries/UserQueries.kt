package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import ru.ac.uniyar.queries.computations.hashPassword
import java.util.*

class UserQueries (
    private val store: Store,
    private val settings: Settings,
    private val userRepository : UserRepository
) {
    inner class AddUserQ {
        operator fun invoke(name: String, phone: String, email: String, password: String, restaurantId:UUID ): UUID {
            val hashedPassword = hashPassword(password, settings.salt)
            val roleId = if(restaurantId == EMPTY_UUID) {
                UUID.fromString("26b9e5e7-1c8a-40e5-aece-2345f6b8afd9")
            }else {
                UUID.fromString("ca3c1157-29d4-4de4-b258-7011c3e7a426")
            }
            val id = userRepository.add(
                User(
                    EMPTY_UUID,
                    name,
                    phone,
                    email,
                    hashedPassword,
                    roleId,
                    restaurantId,
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