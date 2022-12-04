package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import ru.ac.uniyar.queries.computations.hashPassword
import java.util.*

class AddUserQ (
    private val store: Store,
    private val settings: Settings,
    private val userRepository : UserRepository
) {
    operator fun invoke(name: String, phone: String, email: String, password: String,linkToRestaurant:UUID ): UUID {
        val hashedPassword = hashPassword(password, settings.salt)
        if(linkToRestaurant== EMPTY_UUID) {
            val id = userRepository.add(
                User(
                    EMPTY_UUID,
                    name,
                    phone,
                    email,
                    hashedPassword,
                    UUID.fromString("26b9e5e7-1c8a-40e5-aece-2345f6b8afd9"),
                    linkToRestaurant,
                )
            )
            store.save()
            return id;
        }else {
            val id = userRepository.add(
                User(
                    EMPTY_UUID,
                    name,
                    phone,
                    email,
                    hashedPassword,
                    UUID.fromString("ca3c1157-29d4-4de4-b258-7011c3e7a426"),
                    linkToRestaurant,
                )
            )
            store.save()
            return id;
        }

    }
}