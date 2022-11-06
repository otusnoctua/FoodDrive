package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.asJsonValue
import java.util.*

data class RolePermissions(
    val id: UUID,
    val name: String,
    //Списки
    val listOrders: Boolean,
    val listUsers: Boolean,
    val listRestaurants: Boolean,
    val listDishes: Boolean,
    val listReviews: Boolean,
    //Добавление
    val createOrder: Boolean,
    val createUser: Boolean,
    val createRestaurant: Boolean,
    val createDish: Boolean,
    val createReview: Boolean,
    //Показ отдельно взятых сущностей
    val viewOrder: Boolean,
    val viewUser: Boolean,
    val viewRestaurant: Boolean,
    val viewDish: Boolean,
    val viewReview: Boolean,
    //Редактирование сущностей
    val editOrder: Boolean,
    val editUser: Boolean,
    val editRestaurant: Boolean,
    val editDish: Boolean,
    val editReview: Boolean,
    //Удаление отдельно взятых сущностей
    val deleteOrder: Boolean,
    val deleteUser: Boolean,
    val deleteRestaurant: Boolean,
    val deleteDish: Boolean,
    val deleteReview: Boolean,

    ) {
    companion object {
        fun fromJson(jsonNode: JsonNode): RolePermissions {
            val jsonObject = jsonNode.asJsonObject()
            return RolePermissions(
                UUID.fromString(jsonObject["id"].asText()),
                jsonObject["name"].asText(),
                //---------------------------------------
                jsonObject["listOrders"].asBoolean(),
                jsonObject["listUsers"].asBoolean(),
                jsonObject["listRestaurants"].asBoolean(),
                jsonObject["listDishes"].asBoolean(),
                jsonObject["listReviews"].asBoolean(),
                //----------------------------------------
                jsonObject["createOrder"].asBoolean(),
                jsonObject["createUser"].asBoolean(),
                jsonObject["createRestaurant"].asBoolean(),
                jsonObject["createDish"].asBoolean(),
                jsonObject["createReview"].asBoolean(),
                //----------------------------------------
                jsonObject["viewOrder"].asBoolean(),
                jsonObject["viewUser"].asBoolean(),
                jsonObject["viewRestaurant"].asBoolean(),
                jsonObject["viewDish"].asBoolean(),
                jsonObject["viewReview"].asBoolean(),
                //----------------------------------------
                jsonObject["editOrder"].asBoolean(),
                jsonObject["editUser"].asBoolean(),
                jsonObject["editRestaurant"].asBoolean(),
                jsonObject["editDish"].asBoolean(),
                jsonObject["editReview"].asBoolean(),
                //----------------------------------------
                jsonObject["deleteOrder"].asBoolean(),
                jsonObject["deleteUser"].asBoolean(),
                jsonObject["deleteRestaurant"].asBoolean(),
                jsonObject["deleteDish"].asBoolean(),
                jsonObject["deleteReview"].asBoolean(),
                //----------------------------------------

            )
        }

        val ANONYMOUS_ROLE = RolePermissions(
            id = UUID.fromString("a53f3b97-1dd2-4d67-9526-340b6dbb208a"),
            name = "Гость",
            //----------------------------------------
            listOrders = false,
            listUsers = false,
            listRestaurants = true,
            listDishes = true,
            listReviews = true,
            //----------------------------------------
            createOrder = false,
            createUser = true,
            createRestaurant = false,
            createDish = false,
            createReview = false,
            //----------------------------------------
            viewOrder = false,
            viewUser = false,
            viewRestaurant = true,
            viewDish = true,
            viewReview = true,
            //----------------------------------------
            editOrder = false,
            editUser = false,
            editRestaurant = false,
            editDish = false,
            editReview = false,
            //----------------------------------------
            deleteOrder = false,
            deleteUser = false,
            deleteRestaurant = false,
            deleteDish = false,
            deleteReview = false,
            //----------------------------------------

        )
    }

    fun asJsonObject(): JsonNode = listOf(
        "id" to id.toString().asJsonValue(),
        "name" to name.asJsonValue(),
        //----------------------------------------
        "listOrders" to listOrders.asJsonValue(),
        "listUsers" to listUsers.asJsonValue(),
        "listRestaurants" to listRestaurants.asJsonValue(),
        "listDishes" to listDishes.asJsonValue(),
        "listReviews" to listReviews.asJsonValue(),
        //----------------------------------------
        "createOrder" to createOrder.asJsonValue(),
        "createUser" to createUser.asJsonValue(),
        "createRestaurant" to createRestaurant.asJsonValue(),
        "createDish" to createDish.asJsonValue(),
        "createReview" to createReview.asJsonValue(),
        //----------------------------------------
        "viewOrder" to viewOrder.asJsonValue(),
        "viewUser" to viewUser.asJsonValue(),
        "viewRestaurant" to viewRestaurant.asJsonValue(),
        "viewDish" to viewDish.asJsonValue(),
        "viewReview" to viewReview.asJsonValue(),
        //----------------------------------------
        "editOrder" to editOrder.asJsonValue(),
        "editUser" to editUser.asJsonValue(),
        "editRestaurant" to editRestaurant.asJsonValue(),
        "editDish" to editDish.asJsonValue(),
        "editReview" to editReview.asJsonValue(),
        //----------------------------------------
        "deleteOrder" to deleteOrder.asJsonValue(),
        "deleteUser" to deleteUser.asJsonValue(),
        "deleteRestaurant" to deleteRestaurant.asJsonValue(),
        "deleteDish" to deleteDish.asJsonValue(),
        "deleteReview" to deleteReview.asJsonValue(),
        //----------------------------------------

    ).asJsonObject()

    fun setUuid(uuid: UUID): RolePermissions {
        return this.copy(id = uuid)
    }
}
