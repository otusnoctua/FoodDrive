package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*

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

    fun add(order: Order): Int {

        db.orders.add(order)
        return order.id

//        return db.insertAndGenerateKey(Orders){
//            set(it.client_id, order.client.id)
//            set(it.restaurant_id, order.restaurant.id)
//            set(it.order_status, order.orderStatus)
//            set(it.start_time, order.startTime)
//            set(it.end_time, order.endTime)
//            set(it.order_check, order.orderCheck)
//        }.toString().toInt()
    }

    fun addDishToOrder(currentDishId: Int, currentOrderId: Int)  {

        val orderDish = OrderDish {
            dishId = currentDishId
            orderId = currentOrderId
        }

        db.order_dishes.add(orderDish)

//        db.insertAndGenerateKey(OrderDishes) {
//            set(it.dish_id, dishId)
//            set(it.order_id, orderId)
//        }
    }

    fun deleteDishFromOrder(order: Order, dishId: Int){
        db.useTransaction {

            val dishToRemove = db
                .order_dishes
                .find { it.dish_id eq dishId and(it.order_id eq order.id) }!!
                .dishId

            db.delete(OrderDishes) {it.id eq dishToRemove}

        }

    }

    fun updateStatus(order: Order) {

        /* Получение нужного заказа из БД по совпадающему id */
        val orderToChange = db.orders.find { it.id eq order.id }

        /* Изменение статуса заказа на соответствующий статусу order */
        orderToChange?.orderStatus = order.orderStatus

        /* Запись изменений в БД */
        orderToChange?.flushChanges()

    }

    fun delete(id: Int) {
        val order = db.orders.find {it.id eq id}
        order?.delete()
    }

    fun list() : List<Order> {
        return db.orders.toList()
    }
}