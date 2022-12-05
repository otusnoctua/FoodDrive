package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.JsonNode
import org.http4k.format.Jackson.asJsonArray
import java.util.*

class OrderRepository(order:  Iterable<Order> = emptyList()) {
    private val orders = order.associateBy { it.id }.toMutableMap()

    companion object{
        fun fromJson(node: JsonNode) : OrderRepository {
            val orders = node.map{
                Order.fromJson(it)
            }
            return OrderRepository(orders)
        }
    }

    fun asJsonObject(): JsonNode {
        return orders.values
            .map{ it.asJsonObject() }
            .asJsonArray()
    }

    fun fetch(id: UUID): Order? = orders[id]

    fun add(order:  Order): UUID {
        var newId = order.id
        while (orders.containsKey(newId) || newId == EMPTY_UUID){
            newId = UUID.randomUUID()
        }
        orders[newId] = order.setUuid(newId)
        return newId
    }

    fun update(order: Order){
        val id=order.id
        orders[id]= order
    }


    fun delete(id: UUID) {
        orders.remove(id)
    }

    fun edit(order: Order){
        orders[order.id] = order
    }

    fun list() = orders.values.toList()
}