package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import java.time.LocalDateTime

class OrderRepository(
    database: Database
) {
    private val db = database

    fun getUserOrders(userId: Int): List<Order> {
        return db.orders.filter { it.client_id eq userId }.toList()
    }

    fun getOrderStatus(id: Int): String {
        return db.orders.find { it.id eq id }!!.orderStatus
    }

    fun fetch(id: Int): Order? {
        return db.orders.find { it.id eq id }
    }

    fun add(order: Order) : Int {
        return db.orders.add(order)
    }

    fun addDishToOrder(currentDishId: Int, currentOrderId: Int)  {
        val orderDish = OrderDish {
            dishId = currentDishId
            orderId = currentOrderId
        }
        db.order_dishes.add(orderDish)
    }

    fun deleteDishFromOrder(order: Order, dishId: Int) : Order {
        db.useTransaction {

            val dishToRemove = db
                .order_dishes
                .find { it.dish_id eq dishId and(it.order_id eq order.id) }!!
                .dishId

            db.delete(OrderDishes) {it.id eq dishToRemove}

        }
        return order

    }

    fun updateStatus(order: Order) {
        val orderToChange = db.orders.find { it.id eq order.id } ?: return
        orderToChange.orderStatus = order.orderStatus
        orderToChange.flushChanges()

    }

    fun delete(id: Int) {
        val order = db.orders.find {it.id eq id}
        order?.delete()
    }

    fun list() : List<Order> {
        return db.orders.toList()
    }

    fun editStatus(order: Order, status: String) : Order {
        val orderToEdit = db.orders.find { it.id eq order.id } ?: return order
        orderToEdit.orderStatus = status
        orderToEdit.flushChanges()
        return orderToEdit
    }

    fun setPrice(order: Order, price: Int) : Order {
        val orderToEdit = db.orders.find { it.id eq order.id } ?: return order
        orderToEdit.orderCheck = price
        orderToEdit.flushChanges()
        return orderToEdit
    }

    fun getOrderCheck(order: Order) : Int {
        val dishes = db.orders.find { it.id eq order.id }?.dishes

    }

}