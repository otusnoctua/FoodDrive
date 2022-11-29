package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Store
import ru.ac.uniyar.domain.User
import ru.ac.uniyar.domain.UserRepository
import java.util.UUID

class UserQueries(
    private val userRepository: UserRepository,
    private val store: Store,
) {
    inner class UsersQ{
        operator fun invoke(){
            userRepository.list()
        }
    }

    inner class FetchUserQ{
        operator fun invoke(id:UUID): User?{
            return userRepository.fetch(id)
        }
    }

}