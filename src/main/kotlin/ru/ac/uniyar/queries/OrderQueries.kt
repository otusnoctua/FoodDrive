package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
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
    fun fetchOrderViaId(id:UUID):Order
    fun delete(id:UUID)
    fun deleteDish(order: Order, index:Int):Order
    fun editStatus(index: Int,string: String,user_id: UUID)

}
class OrderQueries(
    private val orderRepository: OrderRepository,
    private val dishQueries: DishQueries,
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
        val listOfDishes= mutableListOf<String>()
        listOfDishes.add(dish.nameDish)
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
        return  orderRepository.list().any {it.client_id==user_id && it.status=="В ожидании"}
    }

    override fun addDish(user_id: UUID, id_dish: UUID): Order {
        val order: Order = orderRepository.list().filter { order -> order.client_id ==user_id && order.status=="В ожидании" }.first()
        return order.addElementToDishes(dishQueries.fetchNameDishViaId(id_dish))

    }

    override fun fetchOrdersViaUser_Id(user_id: UUID): List<Order> {
        return orderRepository.list().filter {it.client_id==user_id}
    }

    override fun fetchOrderViaId(id: UUID): Order {
        return orderRepository.fetch(id)!!
    }

    override fun delete(id: UUID) {
        return orderRepository.delete(id)
    }

    override fun deleteDish(order: Order, index: Int): Order {
        val newOrder = order.deleteElementFromDishes(index)
        store.save()
        return newOrder
    }

    override fun editStatus(index: Int, string: String, user_id: UUID){
        val mas= fetchOrdersViaUser_Id(user_id).toMutableList()
        orderRepository.update(mas[index].editStatus(string))
    }

}