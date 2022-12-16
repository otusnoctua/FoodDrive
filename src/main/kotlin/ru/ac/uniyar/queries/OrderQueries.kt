package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.*
import java.time.LocalDateTime

class OrderQueries(
    private val orderRepository: OrderRepository,
) {

    inner class AddOrderQ {
        operator fun invoke(order: Order): Order { //нужно тестирование!
            db.useTransaction {
                orderRepository.add(order)
                return order
            }
        }
    }

        inner class UpdateOrderQ {
            operator fun invoke(order: Order) {
                orderRepository.update(order)
            }
        }

        inner class CreateOrderQ {
            operator fun invoke(user: User, dish: Dish): Order {
                val order = Order {
                    client = user
                    restaurant = dish.restaurant
                    orderStatus = "В ожидании"
                    startTime = LocalDateTime.now()
                    orderCheck = 0
                }
                AddOrderQ().invoke(order)
                return orderRepository.addDishToOrder(dish.id, order.id)
            }
        }

        inner class CheckOrderQ {
            operator fun invoke(userId: Int, restaurantId: Int): Boolean {
                return orderRepository.list().any { it.client.id == userId && it.orderStatus == "В ожидании" && it.restaurant.id == restaurantId}
            }
        }

        inner class CheckOrderRestaurantQ {
            operator fun invoke(userId: Int, restaurant: Int): Boolean {
                return orderRepository.list()
                    .any { it.client.id == userId && it.restaurant.id == restaurant && it.orderStatus == "В ожидании" }
            }
        }

        inner class FetchOrderCheck {
            operator fun invoke(order: Order) : Int {
                return orderRepository.getOrderCheck(order)
            }
        }


        inner class FetchOrderDishes {
            operator fun invoke(order: Order) : List<Dish> {
                return order.dishes
            }
        }

        inner class AddDishQ {
            operator fun invoke(userId: Int, dish: Dish): Order {
                val order: Order =
                    orderRepository.list()
                        .first { it.client.id == userId && it.restaurant.id == dish.restaurant.id && it.orderStatus == "В ожидании" }
                return orderRepository.addDishToOrder(dish.id, order.id)
            }
        }

        inner class OrdersForUserQ {
            operator fun invoke(userId: Int): List<Order> {
                return orderRepository.list().filter { it.client.id == userId }
            }
        }

        inner class OrdersForOperatorQ {
            operator fun invoke(restaurantId: Int): List<Order> {
                return orderRepository.list()
                    .filter { it.orderStatus != "В ожидании" && it.orderStatus != "Готов" && it.restaurant.id == restaurantId }
            }
        }

        inner class WaitingOrdersQ {
            operator fun invoke(userId: Int): List<Order> {
                return orderRepository.list().filter { it.client.id == userId && it.orderStatus == "В ожидании" }
            }
        }

        inner class AcceptedOrdersQ {
            operator fun invoke(userId: Int): List<Order> {
                return orderRepository.list().filter { it.client.id == userId && it.orderStatus != "В ожидании" }
            }
        }

        inner class AcceptedOrdersFromRestaurantQ {
            operator fun invoke(userId: Int, restaurantId: Int): Boolean {
                return orderRepository.list().any { it.client.id == userId && it.restaurant.id == restaurantId && it.orderStatus != "В ожидании"}
            }
        }

        inner class FetchOrderQ {
            operator fun invoke(id: Int): Order? {
                return orderRepository.fetch(id)
            }
        }

        inner class DeleteOrderQ {
            operator fun invoke(order: Order) {
                orderRepository.delete(order)
            }
        }

        inner class DeleteDishQ {
            operator fun invoke(order: Order, dishId: Int): Order {
                return orderRepository.deleteDishFromOrder(order, dishId)
            }
        }

        inner class EditStatusQ {
            operator fun invoke(order: Order, string: String): Order {
                return orderRepository.editStatus(order, string)
            }
        }

        inner class SetPriceQ {
            operator fun invoke(order: Order, price: Int): Order {
                return orderRepository.setPrice(order, price)
            }
        }

        inner class SetEndTimeQ {
            operator fun invoke(order: Order): Order {
                return orderRepository.setEndTime(order)
            }
        }
    }