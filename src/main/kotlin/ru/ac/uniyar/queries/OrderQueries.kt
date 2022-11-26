package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import java.time.LocalDateTime
import java.util.*

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
            orderRepository.update(order)
        }
    }

    inner class CreateOrder{
        operator fun invoke(userId: Int, dish: Dish):Order{
            val listOfDishes= mutableListOf<Int>()
            listOfDishes.add(dish.id)
            val order: Order = Order(
                UUID.randomUUID(),
                userId,
                dish.restaurantId,
                "В ожидании",
                LocalDateTime.now(),
                listOfDishes,
            )
            AddOrder().invoke(order)
            return order
        }
    }
    inner class CheckOrder{
        operator fun invoke(userId: Int):Boolean{
            return orderRepository.list().any {it.clientId==userId && it.status=="В ожидании"}
        }
    }
    inner class AddDish{
        operator fun invoke(userId: Int, dishId: Int):Order{
            val order: Order = orderRepository.list().filter { order -> order.clientId == userId && order.status=="В ожидании" }.first()
            return order.addElementToDishes(dishId.toString())
        }
    }

    inner class FetchOrdersViaUserId{
        operator fun invoke(userId: Int):List<Order>{
            return orderRepository.list().filter {it.clientId==userId}
        }
    }
    inner class FetchOrderViaId{
        operator fun invoke(id: UUID):Order?{
            return orderRepository.fetch(id)
        }
    }
    inner class DeleteOrder{
        operator fun invoke(id: UUID){
            return orderRepository.delete(id)
        }
    }
   inner class DeleteDish{
       operator fun invoke(order: Order,index: Int):Order{
           val newOrder = order.deleteElementFromDishes(index)
           store.save()
           return newOrder
       }
   }

    inner class EditStatus{
        operator fun invoke(index: Int,string: String,userId: Int){
            val mas = FetchOrdersViaUserId().invoke(userId).toMutableList()
            orderRepository.update(mas[index].editStatus(string))
        }
    }


}