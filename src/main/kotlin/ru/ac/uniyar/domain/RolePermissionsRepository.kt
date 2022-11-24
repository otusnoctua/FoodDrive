package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import ru.ac.uniyar.database.Roles

class RolePermissionsRepository(
    database : Database
){

    private val db = database
    fun fetch(id: Int): RolePermissions{
        return db
            .from(Roles)
            .select()
            .where{Roles.id.toInt() eq id }
            .map { RolePermissions(
                it.getInt(1),
                it.getString(2)!!,
                it.getBoolean(3),
                it.getBoolean(4),
                it.getBoolean(5),
                it.getBoolean(6),
                it.getBoolean(7),
                it.getBoolean(8),
                it.getBoolean(9),
                it.getBoolean(10),
                it.getBoolean(11),
                it.getBoolean(12),
                it.getBoolean(13),
                it.getBoolean(14),
                it.getBoolean(15),
                it.getBoolean(16),
                it.getBoolean(17),
                it.getBoolean(18),
                it.getBoolean(19),
                it.getBoolean(20),
                it.getBoolean(21),
                it.getBoolean(22),
                it.getBoolean(23),
                it.getBoolean(24),
                it.getBoolean(25),
                it.getBoolean(26),
                it.getBoolean(27),
            ) }.first()
    }

     fun add(rolePermissions: RolePermissions) : Int {
        return db.insertAndGenerateKey(Roles) {
            set(it.name, rolePermissions.name)
            set(it.list_orders, rolePermissions.listOrders)
            set(it.list_users, rolePermissions.listUsers)
            set(it.list_restaurants, rolePermissions.listRestaurants)
            set(it.list_dishes, rolePermissions.listDishes)
            set(it.list_reviews, rolePermissions.listReviews)
            set(it.create_order, rolePermissions.createOrder)
            set(it.create_user, rolePermissions.createUser)
            set(it.create_restaurant, rolePermissions.createRestaurant)
            set(it.create_dish, rolePermissions.createDish)
            set(it.create_review, rolePermissions.createReview)
            set(it.view_order, rolePermissions.viewOrder)
            set(it.view_user, rolePermissions.viewUser)
            set(it.view_restaurant, rolePermissions.viewRestaurant)
            set(it.view_dish, rolePermissions.viewDish)
            set(it.view_review, rolePermissions.viewReview)
            set(it.edit_order, rolePermissions.editOrder)
            set(it.edit_user, rolePermissions.editUser)
            set(it.edit_restaurant, rolePermissions.editRestaurant)
            set(it.edit_dish, rolePermissions.editDish)
            set(it.edit_review, rolePermissions.editReview)
            set(it.delete_order, rolePermissions.deleteOrder)
            set(it.delete_user, rolePermissions.deleteUser)
            set(it.delete_restaurant, rolePermissions.deleteRestaurant)
            set(it.delete_dish, rolePermissions.deleteDish)
            set(it.delete_review, rolePermissions.deleteReview)

        }.toString().toInt()
    }

    fun list() : List<RolePermissions> {
        return db.from(Roles).select().map {
            RolePermissions(
                it.getInt(1),
                it.getString(2)!!,
                it.getBoolean(3),
                it.getBoolean(4),
                it.getBoolean(5),
                it.getBoolean(6),
                it.getBoolean(7),
                it.getBoolean(8),
                it.getBoolean(9),
                it.getBoolean(10),
                it.getBoolean(11),
                it.getBoolean(12),
                it.getBoolean(13),
                it.getBoolean(14),
                it.getBoolean(15),
                it.getBoolean(16),
                it.getBoolean(17),
                it.getBoolean(18),
                it.getBoolean(19),
                it.getBoolean(20),
                it.getBoolean(21),
                it.getBoolean(22),
                it.getBoolean(23),
                it.getBoolean(24),
                it.getBoolean(25),
                it.getBoolean(26),
                it.getBoolean(27),
            )
        }
    }
}