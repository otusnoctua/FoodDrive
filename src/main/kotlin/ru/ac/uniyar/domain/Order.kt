package ru.ac.uniyar.domain

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.*
import java.time.LocalDateTime

val db = Database.connect(
    url = "jdbc:mysql://127.0.0.1:3306/fooddrive",
    driver = "com.mysql.jdbc.Driver",
    user = "root",
    password = "12345"
)

interface Order : Entity<Order> {
    companion object : Entity.Factory<Order>()
    val id: Int
    var client: User
    var restaurant: Restaurant
    var orderStatus: String
    var startTime: LocalDateTime
    var endTime: LocalDateTime
    val dishes get() = getDishesViaOrderId(id)
    var orderCheck: Int
}

object Orders : Table<Order>("orders"){
    val id = int("id").primaryKey().bindTo { it.id }
    val client_id = int("client_id").references(Users) { it.client }
    val restaurant_id = int("restaurant_id").references(Restaurants) { it.restaurant }
    val order_status = varchar("order_status").bindTo { it.orderStatus }
    val start_time = datetime("start_time").bindTo { it.startTime }
    val end_time = datetime("end_time").bindTo { it.endTime }
    val order_check = int("order_check").bindTo { it.orderCheck }

    //add constraint
}

val Database.orders get() = this.sequenceOf(Orders)

inline fun <E : Any, T : BaseTable<E>> T.getList(predicate: (T) -> ColumnDeclaring<Boolean>): List<E> {
    return db.sequenceOf(this).filter(predicate).toList()
}

fun getDishesViaOrderId(id: Int) : List<Dish> {
    db.useTransaction {
        val dishIds = db.order_dishes.filter { it.order_id eq id }.map { it.dishId }
        return dishIds.map { dishId -> db.dishes.find { it.id eq dishId }!! }
    }
}