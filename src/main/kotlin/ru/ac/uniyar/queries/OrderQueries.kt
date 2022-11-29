package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import java.time.LocalDateTime
import java.util.*


class OrderQueries(
    private val orderRepository: OrderRepository,
    private val dishQueries: DishQueries,
    private val store: Store,
) {

    inner class OrdersQ{
        operator fun invoke(){
            orderRepository.list()
        }
    }

   inner class AddOrderQ{
       operator fun invoke(order: Order){
           orderRepository.add(order)
       }
   }

    inner class UpdateOrderQ{
        operator fun invoke(order: Order){
            orderRepository.update(order)
        }
    }

    inner class CreateOrderQ{
        operator fun invoke(userId:UUID, dish: Dish):Order{
            val listOfDishes= mutableListOf<String>()
            listOfDishes.add(dish.nameDish)
            val order: Order = Order(
                UUID.randomUUID(),
                userId,
                dish.restaurantId,
                "В ожидании",
                LocalDateTime.now(),
                listOfDishes,
            )
            AddOrderQ().invoke(order)
            return order
        }
    }
    inner class CheckOrderQ{
        operator fun invoke(userId: UUID):Boolean{
            return  orderRepository.list().any {it.clientId==userId && it.status=="В ожидании"}
        }
    }
    inner class AddDishQ{
        operator fun invoke(userId: UUID,dishId:UUID):Order{
            val order: Order =
                orderRepository.list().first { it.clientId == userId && it.status == "В ожидании" }
            return order.addElementToDishes(dishQueries.FetchDishQ().invoke(dishId)?.nameDish ?: "")
        }
    }

    inner class OrdersByUserQ{
        operator fun invoke(userId: UUID):List<Order>{
            return orderRepository.list().filter {it.clientId==userId}
        }
    }
    inner class FetchOrderQ{
        operator fun invoke(id: UUID):Order?{
            return orderRepository.fetch(id)
        }
    }
    inner class DeleteOrderQ{
        operator fun invoke(id: UUID){
            return orderRepository.delete(id)
        }
    }
   inner class DeleteDishQ{
       operator fun invoke(order: Order,index: Int):Order{
           val newOrder = order.deleteElementFromDishes(index)
           store.save()
           return newOrder
       }
   }

    inner class EditStatusQ{
        operator fun invoke(index: Int,string: String,userId: UUID){
            val mas = OrdersByUserQ().invoke(userId).toMutableList()
            orderRepository.update(mas[index].editStatus(string))
        }
    }


}