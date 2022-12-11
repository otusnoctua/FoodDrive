package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.*
import java.time.LocalDateTime

val db = Database.connect(
    url = "jdbc:mysql://127.0.0.1:3306/fooddrive",
    driver = "com.mysql.jdbc.Driver",
    user = "fda",
    password = "poorsara"
)

interface Order : Entity<Order> {
    val id: Int
    var client: User
    var restaurant: Restaurant
    var orderStatus: String
    var startTime: LocalDateTime
    var endTime: LocalDateTime
    //val dishes get() = Dishes.getList { it.id eq id }
    val dishes get() = db.order_dishes.filter { it.order_id eq id }
    var orderCheck: Double

    fun addElementToDishes(nameDish: String) : Order{
        val mas = this.dishes.toMutableList()
        mas.add(nameDish.toInt())
        return this.copy(dishes = mas)
    }
    fun deleteElementFromDishes(index:Int):Order{
        val mas= this.dishes.toMutableList()
        mas.removeAt(index)
        return this.copy(dishes = mas)
    }

    fun editStatus(status: String) : Order{
        return this.copy(status = status)
    }

}

object Orders : Table<Order>("orders"){
    val id = int("id").primaryKey().bindTo { it.id }
    val client_id = int("client_id").references(Users) { it.client }
    val restaurant_id = int("restaurant_id").references(Restaurants) { it.restaurant }
    val order_status = varchar("order_status").bindTo { it.orderStatus }
    val start_time = datetime("start_time").bindTo { it.startTime }
    val end_time = datetime("end_time").bindTo { it.endTime }
    val dishes get() = OrderDishes.getList {it.order_id eq id }
    val order_check = double("order_check").bindTo { it.orderCheck }
}

val Database.orders get() = this.sequenceOf(Orders)

inline fun <E : Any, T : BaseTable<E>> T.getList(predicate: (T) -> ColumnDeclaring<Boolean>): List<E> {
    return db.sequenceOf(this).filter(predicate).toList()
}