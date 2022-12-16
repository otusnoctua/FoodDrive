package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.*
import java.time.LocalDateTime

interface Order : Entity<Order> {
    companion object : Entity.Factory<Order>()
    val id: Int
    var client: User
    var restaurant: Restaurant
    var orderStatus: String
    var startTime: LocalDateTime
    var endTime: LocalDateTime?
    val dishes get() = Store.getDishesViaOrderId(id)
    var orderCheck: Int
}

object Orders : Table<Order>("orders"){
    val id = int("id").primaryKey().bindTo { it.id }
    val client_id = int("client_id").references(Users) { it.client }
    val restaurant_id = int("restaurant_id").references(Restaurants) { it.restaurant }
    val order_status = varchar("order_status").bindTo { it.orderStatus }
    val start_time = datetime("start_time").bindTo { it.startTime }
    val end_time = datetime("end_time").bindTo { it.endTime }
    val order_check = int("order_check").bindTo { it.orderCheck }

    //add constraint
}

val Database.orders get() = this.sequenceOf(Orders)
