package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.asPrettyJsonString
import org.ktorm.database.Database
import java.nio.file.Path
import kotlin.concurrent.thread
import kotlin.io.path.isReadable

class Store(
    private val documentStoragePath: Path,
    database: Database
    ) {
    val storeThread = thread(start = false, name = "Store file save") {
        save()
    }
    val rolePermissionsRepository: RolePermissionsRepository
    val dishRepository: DishRepository
    val restaurantRepository: RestaurantRepository
    val orderRepository: OrderRepository
    val reviewRepository: ReviewRepository
    val userRepository:UserRepository


    init {
        val node = if (documentStoragePath.isReadable()) {
            val file = documentStoragePath.toFile()
            val jsonDocument = file.readText()
            Jackson.parse(jsonDocument)
        } else null

        rolePermissionsRepository = RolePermissionsRepository(emptyList())

        dishRepository = DishRepository(database)

        restaurantRepository = RestaurantRepository(database)

        orderRepository = if (node != null && node.has("order")) {
            OrderRepository.fromJson(node["order"])
        } else {
            OrderRepository()
        }
        reviewRepository = ReviewRepository(database)

        userRepository = UserRepository(database)



    }

    fun save() {
        val document: JsonNode =
            listOf(
                "rolePermissions" to rolePermissionsRepository.asJsonObject(),
                "dish" to dishRepository.asJsonObject(),
                "restaurant" to restaurantRepository.asJsonObject(),
                "order" to orderRepository.asJsonObject(),
                "review" to reviewRepository.asJsonObject(),
                "user" to userRepository.asJsonObject(),
            ).asJsonObject()
        val documentString = document.asPrettyJsonString()
        val file = documentStoragePath.toFile()
        file.writeText(documentString)
    }
}