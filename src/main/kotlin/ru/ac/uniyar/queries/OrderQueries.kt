package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import java.time.LocalDateTime
import java.util.*


class OrderQueries(
    private val orderRepository: OrderRepository,
    private val store: Store,
) {

   inner class AddOrderQ{
       operator fun invoke(order: Order){
           orderRepository.add(order)
           store.save()
       }
   }

    inner class UpdateOrderQ{
        operator fun invoke(order: Order){
            orderRepository.update(order)
            store.save()
        }
    }

    inner class CreateOrderQ{
        operator fun invoke(userId:UUID, dish: Dish):Order{
            val listOfDishes= mutableListOf<UUID>()
            listOfDishes.add(dish.id)
            val order = Order(
                UUID.randomUUID(),
                userId,
                dish.restaurantId,
                "В ожидании",
                LocalDateTime.now(),
                listOfDishes,
                0,
            )
            AddOrderQ().invoke(order)
            return order
        }
    }
    inner class CheckOrderQ{
        operator fun invoke(userId: UUID): Boolean{
            return  orderRepository.list().any {it.clientId == userId && it.status == "В ожидании"}
        }
    }
    inner class CheckOrderRestaurantQ{
        operator fun invoke(userId: UUID, restaurant: UUID):Boolean{
            return  orderRepository.list().any {it.clientId == userId && it.restaurantId == restaurant && it.status == "В ожидании"}
        }
    }
    inner class AddDishQ{
        operator fun invoke(userId: UUID, dish:Dish):Order{
            val order: Order =
                orderRepository.list().first { it.clientId == userId && it.restaurantId == dish.restaurantId && it.status == "В ожидании" }
            return order.addDish(dish.id)
        }
    }

    inner class OrdersForUserQ{
        operator fun invoke(userId: UUID):List<Order>{
            return orderRepository.list().filter { it.clientId == userId }
        }
    }

    inner class OrdersForOperatorQ {
        operator fun invoke(restaurantId: UUID): List<Order> {
            return orderRepository.list().filter { it.status != "В ожидании" && it.restaurantId == restaurantId }
        }
    }
    inner class WaitingOrdersQ{
        operator fun invoke(userId: UUID):List<Order>{
            return orderRepository.list().filter {it.clientId == userId && it.status == "В ожидании"}
        }
    }
    inner class AcceptedOrdersQ{
        operator fun invoke(userId: UUID):List<Order>{
            return orderRepository.list().filter {it.clientId == userId && it.status != "В ожидании"}
        }
    }
    inner class FetchOrderQ{
        operator fun invoke(id: UUID):Order?{
            return orderRepository.fetch(id)
        }
    }
    inner class DeleteOrderQ{
        operator fun invoke(id: UUID){
            orderRepository.delete(id)
            store.save()
        }
    }
    inner class DeleteDishQ {
        operator fun invoke(order: Order, dishId: UUID): Order {
            val newOrder = order.deleteDish(dishId)
            orderRepository.update(newOrder)
            store.save()
            return newOrder
        }
    }

    inner class EditStatusQ{
        operator fun invoke(order: Order,string: String): Order{
            val newOrder = order.editStatus(string)
            orderRepository.update(newOrder)
            store.save()
            return newOrder
        }
    }

    inner class SetPriceQ{
        operator fun invoke(order: Order, price: Int): Order{
            val newOrder = order.setPrice(price)
            orderRepository.update(newOrder)
            store.save()
            return newOrder
        }
    }
}