package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*

interface OrderDish: Entity<OrderDish> {
    companion object : Entity.Factory<OrderDish>()
    val id: Int
    var orderId: Int
    var dishId: Int
}

object OrderDishes : Table<OrderDish>("order_dishes"){
    val id = int("id").primaryKey().bindTo { it.id }
    val order_id = int("order_id").bindTo { it.orderId }
    val dish_id = int("dish_id").bindTo { it.dishId }
}

val Database.order_dishes get() = this.sequenceOf(OrderDishes)