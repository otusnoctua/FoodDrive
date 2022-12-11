package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import java.time.LocalDateTime

class OrderQueries(
    private val orderRepository: OrderRepository,
    private val dishQueries: DishQueries,
    private val store: Store,
) {

    inner class ListOfOrders{
        operator fun invoke(){
            orderRepository.list()
        }
    }

   inner class AddOrder{
       operator fun invoke(order: Order){
           orderRepository.add(order)
       }
   }

    inner class UpdateOrder{
        operator fun invoke(order: Order){
            orderRepository.updateStatus(order)
        }
    }

    inner class CreateOrder{
        operator fun invoke(
            userId: Int,
            dish: Dish
        ):Order{
            val listOfDishes = mutableListOf<Int>()
            listOfDishes.add(dish.id)

            val order = Order {
                0
                client = userId
                dish.restaurant.id,
                "В ожидании",
                LocalDateTime.now(),
                listOfDishes,
            }

            AddOrder().invoke(order)
            return order
        }
    }
    inner class CheckOrder{
        operator fun invoke(userId: Int) : Boolean{
            return orderRepository.getUserOrders(userId).any { it.orderStatus == "В ожидании" }
        }
    }

    inner class AddDish{
        operator fun invoke(userId: Int, dishId: Int){
            val order: Order = orderRepository.getUserOrders(userId).find { it.orderStatus == "В ожидании" }!!
            return orderRepository.addDishToOrder(dishId, order.id)
        }
    }

    inner class FetchOrdersViaUserId{
        operator fun invoke(userId: Int):List<Order>{
            return orderRepository.list().filter {it.client.id == userId}
        }
    }
    inner class FetchOrderViaId{
        operator fun invoke(id: Int):Order?{
            return orderRepository.fetch(id)
        }
    }
    inner class DeleteOrder{
        operator fun invoke(id: Int){
            return orderRepository.delete(id)
        }
    }
   inner class DeleteDish{
       operator fun invoke(order: Order, dishId: Int) : Int? {
           return try {
               orderRepository.deleteDishFromOrder(order, dishId)
               1
           } catch (e: NoSuchElementException) {
               null
           }
       }
   }

    inner class EditStatus{
        operator fun invoke(index: Int,string: String,userId: Int){
            val mas = FetchOrdersViaUserId().invoke(userId).toMutableList()
            orderRepository.updateStatus(mas[index].editStatus(string))
        }
    }


}