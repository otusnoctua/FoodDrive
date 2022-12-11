package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import java.time.LocalDateTime
import java.util.*


class OrderQueries(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val store: Store,
) {

   inner class AddOrderQ{
       operator fun invoke(
           currentClient : User,
           currentRestaurant : Restaurant,
           currentOrderStatus : String,
           currentStartTime : LocalDateTime,
           currentOrderCheck : Int
       ) : Order {
         return orderRepository.add(
             currentClient,
             currentRestaurant,
             currentOrderStatus,
             currentStartTime,
             currentOrderCheck
         )
       }
   }

    inner class UpdateOrderQ{
        operator fun invoke(order: Order){
            orderRepository.update(order)
        }
    }

    inner class CreateOrderQ{
        operator fun invoke(user : User, dish: Dish) : Order {
            val listOfDishes = mutableListOf<Dish>()
            listOfDishes.add(dish)
            return AddOrderQ().invoke(
                user,
                dish.restaurant,
                "В ожидании",
                LocalDateTime.now(),
                0,
            )
        }
    }
    inner class CheckOrderQ{
        operator fun invoke(userId: Int): Boolean{
            return  orderRepository.list().any {it.client.id == userId && it.orderStatus == "В ожидании"}
        }
    }
    inner class CheckOrderRestaurantQ{
        operator fun invoke(userId: Int, restaurant: Int):Boolean{
            return  orderRepository.list().any {it.client.id == userId && it.restaurant.id == restaurant && it.orderStatus == "В ожидании"}
        }
    }
    inner class AddDishQ{
        operator fun invoke(userId: Int, dish:Dish):Order{
            val order: Order =
                orderRepository.list().first { it.client.id == userId && it.restaurant.id == dish.restaurant.id && it.orderStatus == "В ожидании" }
            return order.addDish(dish.id)
        }
    }

    inner class OrdersForUserQ{
        operator fun invoke(userId: Int):List<Order>{
            return orderRepository.list().filter { it.client.id == userId }
        }
    }

    inner class OrdersForOperatorQ {
        operator fun invoke(restaurantId: Int): List<Order> {
            return orderRepository.list().filter { it.orderStatus != "В ожидании" && it.restaurant.id == restaurantId }
        }
    }
    inner class WaitingOrdersQ{
        operator fun invoke(userId: Int):List<Order>{
            return orderRepository.list().filter {it.client.id == userId && it.orderStatus == "В ожидании"}
        }
    }
    inner class AcceptedOrdersQ{
        operator fun invoke(userId: Int):List<Order>{
            return orderRepository.list().filter {it.client.id == userId && it.orderStatus != "В ожидании"}
        }
    }
    inner class FetchOrderQ{
        operator fun invoke(id: Int):Order?{
            return orderRepository.fetch(id)
        }
    }
    inner class DeleteOrderQ{
        operator fun invoke(id: Int){
            orderRepository.delete(id)
        }
    }
    inner class DeleteDishQ {
        operator fun invoke(order: Order, dishId: Int): Order {
            return orderRepository.deleteDishFromOrder(order, dishId)
        }
    }

    inner class EditStatusQ{
        operator fun invoke(order: Order, string: String): Order{
            return orderRepository.editStatus(order, string)
        }
    }

    inner class SetPriceQ{
        operator fun invoke(order: Order, price: Int): Order{
            val newOrder = order.setPrice(price)
            orderRepository.update(newOrder)
            return newOrder
        }
    }
}