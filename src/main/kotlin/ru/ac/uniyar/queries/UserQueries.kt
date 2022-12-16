package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import ru.ac.uniyar.queries.computations.hashPassword

class UserQueries (
    private val settings: Settings,
    private val userRepository : UserRepository
) {
    inner class AddUserQ {
        operator fun invoke(
            userName: String,
            userPhone: Long,
            userEmail: String,
            userPassword: String,
            userRestaurant : Restaurant?
        ): Int {
            val userHashedPassword = hashPassword(userPassword, settings.salt)
            var userRoleId = if(userRestaurant?.id == null) {
                1
            } else {
                2
            }
            if(ListOfUsersQuery().invoke().isEmpty()){
                userRoleId = RolePermissions.ADMIN_ROLE.id
            }
            val id = userRepository.add(
                User {
                    username = userName
                    phone = userPhone
                    email = userEmail
                    hashedPassword = userHashedPassword
                    roleId = userRoleId
                    restaurant = userRestaurant
                }
            )
            return id;
        }
    }
    inner class FetchUserViaId {
        operator fun invoke(id: Int): User? {
            return userRepository.fetch(id)
        }
    }
    inner class ListOfUsersQuery {
        operator fun invoke(): List<User> {
            return userRepository.list()
        }
    }
    inner class EditUserQuery {
        operator fun invoke(userName: String, userPhone: Long, userEmail: String, user: User){
            userRepository.changeUser(userName, userPhone, userEmail, user)
        }
    }
}