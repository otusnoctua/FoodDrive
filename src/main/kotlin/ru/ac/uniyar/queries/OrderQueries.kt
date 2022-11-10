package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Dish
import ru.ac.uniyar.domain.Order
import ru.ac.uniyar.domain.OrderRepository
import ru.ac.uniyar.domain.Store
import java.time.LocalDateTime
import java.util.*

interface OrderQuery {
    fun list(): List<Order>
    fun add(order: Order)
    fun update(order: Order)
    fun addDish(user_id: UUID, id_dish: UUID): Order
    fun create(user_id: UUID, dish: Dish): Order
    fun check(user_id: UUID):Boolean
    fun fetchOrdersViaUser_Id(user_id: UUID):List<Order>

}
class OrderQueries(
    private val orderRepository: OrderRepository,
    private val store: Store,
): OrderQuery {
    override fun list() = orderRepository.list()

    override fun add(order: Order) {
        orderRepository.add(order)
    }

    override fun update(order: Order) {
        orderRepository.update(order)
    }

    override fun create(user_id: UUID, dish: Dish): Order {
        val listOfDishes= mutableListOf<UUID>()
        listOfDishes.add(dish.id)
        val order: Order = Order(
            UUID.randomUUID(),
            user_id,
            dish.restaurant_id,
            "В ожидании",
            LocalDateTime.now(),
            listOfDishes,
        )
        add(order)
        return order
    }

    override fun check(user_id: UUID): Boolean {
        return  orderRepository.list().any {it.client_id==user_id}
    }

    override fun addDish(user_id: UUID, id_dish: UUID): Order {
        val order: Order = orderRepository.list().filter { order -> order.client_id ==user_id && order.status=="В ожидании" }.first()
        return order.addElementToDishes(id_dish)

    }

    override fun fetchOrdersViaUser_Id(user_id: UUID): List<Order> {
        return orderRepository.list().filter {it.client_id==user_id}
    }


}