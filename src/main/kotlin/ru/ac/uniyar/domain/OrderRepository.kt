package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import ru.ac.uniyar.database.OrderDish
import ru.ac.uniyar.database.Orders
import java.util.*

class OrderRepository(
    database: Database
) {
    private val db = database

    fun getUserOrders(userId: Int): List<Order>{
            return db
                .from(Orders)
                .select()
                .where { Orders.client_id.toInt() eq userId }
                .map { order ->
                    Order(
                        order.getInt(1),
                        order.getInt(2),
                        order.getInt(3),
                        order.getString(4)!!,
                        order.getLocalDateTime(5)!!,
                        db
                            .from(OrderDish)
                            .leftJoin(Orders, on = Orders.id eq OrderDish.order_id)
                            .select(OrderDish.dish_id)
                            .where { OrderDish.order_id eq order.getInt(1) }
                            .map { it.getInt(1) }
                            .toList()

                    )
                }.toList()
    }

    fun getOrderStatus(id: Int): String {
        return db
            .from(Orders)
            .select(Orders.status)
            .where{ Orders.id eq id }
            .map {
                it.getString(1)!!
            }
            .first()
    }

    fun fetch(id: Int): Order {
        db.useTransaction {
            val dishIdList = db
                .from(OrderDish)
                .leftJoin(Orders, on = Orders.id eq OrderDish.order_id)
                .select(OrderDish.dish_id)
                .where { OrderDish.order_id eq id }
                .map { it.getInt(1) }
                .toList()
            return db
                .from(Orders)
                .select()
                .where { Orders.id.toInt() eq id }
                .map {
                    Order(
                        it.getInt(1),
                        it.getInt(2),
                        it.getInt(3),
                        it.getString(4)!!,
                        it.getLocalDateTime(5)!!,
                        dishIdList
                    )
                }.first()
        }
    }

    fun add(order: Order): Int {
        return db.insertAndGenerateKey(Orders){
            set(it.client_id, order.clientId)
            set(it.restaurant_id, order.restaurantId)
            set(it.status, order.status)
            set(it.datetime, order.datetime)
        }.toString().toInt()
    }

    fun addDishToOrder(dishId: Int, orderId: Int)  {
        db.insertAndGenerateKey(OrderDish) {
            set(it.dish_id, dishId)
            set(it.order_id, orderId)
        }
    }

    fun deleteDishFromOrder(order: Order, dishId: Int){
        db.useTransaction {
            val dishToDeleteId = db
                .from(OrderDish)
                .select(OrderDish.id)
                .where { OrderDish.dish_id eq dishId and(OrderDish.order_id eq order.id) }
                .map { it.getInt(1) }
                .first()
            db.delete(OrderDish) {it.id eq dishToDeleteId}
        }

    }

    fun updateStatus(order: Order){
        db.update(Orders){
            set(it.status, order.status)
            where {
                it.id eq order.id
            }
        }
    }

    fun delete(id: Int) {
        db.useTransaction {
            db.delete(OrderDish) { it.order_id eq id }
            db.delete(Orders) { it.id eq id }
        }
    }

    fun list() : List<Order> {
        return db
            .from(Orders)
            .select()
            .map { order ->
                Order(
                    order.getInt(1),
                    order.getInt(2),
                    order.getInt(3),
                    order.getString(4)!!,
                    order.getLocalDateTime(5)!!,
                    db
                        .from(OrderDish)
                        .leftJoin(Orders, on = Orders.id eq OrderDish.order_id)
                        .select(OrderDish.dish_id)
                        .where { OrderDish.order_id eq order.getInt(1) }
                        .map { it.getInt(1) }
                        .toList()

                )
            }.toList()
    }
}