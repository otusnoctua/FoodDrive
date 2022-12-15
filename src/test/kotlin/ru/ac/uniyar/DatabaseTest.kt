package ru.ac.uniyar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import ru.ac.uniyar.domain.Settings
import ru.ac.uniyar.domain.Store
import ru.ac.uniyar.domain.User
import ru.ac.uniyar.domain.users
import ru.ac.uniyar.queries.UserQueries
import java.nio.file.Path

class DatabaseTest {

    private val database = Database.connect(
        url = "jdbc:mysql://127.0.0.1:3305/fooddrive",
        driver = "com.mysql.jdbc.Driver",
        user = "root",
        password = "12345"
    )

    private val store = Store(database)
    private val settings = Settings(Path.of("settings.json"))

    val userQueries = UserQueries(store, settings, store.userRepository)


    /* Проверяет добавление пользователя и соответствие роли клиента, если пользователю не назначен ресторан */
    @Test
    fun addNewUser() {
        val alice = User {
            username = "Alice"
            phone = 123
            email = "alice@example.com"
            hashedPassword = "hashed_password_here"
            roleId = 1
        }
        database.useTransaction {
            val addQuery = userQueries.AddUserQ().invoke(
                "Alice",
                123,
                "alice@example.com",
                "hashed_password_here",
                null
            )
            val fetchUser = database.users.find { it.id eq addQuery }?.id
            val fetchRole = database.users.find { it.id eq addQuery }?.roleId
            assertEquals(fetchUser, 1)
            assertEquals(fetchRole, 1)
        }

    }

}