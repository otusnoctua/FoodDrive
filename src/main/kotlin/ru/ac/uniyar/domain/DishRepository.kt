package ru.ac.uniyar.domain

import com.fasterxml.jackson.databind.BeanDescription
import org.ktorm.database.Database
import org.ktorm.dsl.*
import ru.ac.uniyar.database.Dishes

class DishRepository(
    database: Database
) {
    private val db = database


    fun fetch(id: Int) : Dish {
        return db
            .from(Dishes)
            .select()
            .where{ Dishes.id.toInt() eq id }
            .map {  Dish(
                it.getInt(1),
                it.getString(2)!!,
                it.getInt(3),
                it.getString(4)!!,
                it.getBoolean(5),
                it.getString(6)!!,
            ) }.first()

    }

    fun add(dish: Dish) : Int {
        return db.insertAndGenerateKey(Dishes){
            set(it.name, dish.name)
            set(it.restaurant_id, dish.restaurantId)
            set(it.ingredients, dish.ingredients)
            set(it.vegan, dish.vegan)
            set(it.description, dish.description)
        }.toString().toInt()
    }

    fun delete(id: Int) {
        db.delete(Dishes) { it.id eq id }
    }

    fun edit(
        name: String,
        ingredients: String,
        vegan: Boolean,
        description: String,
        dish: Dish)
    {
        db.update(Dishes){
            set(it.name, name)
            set(it.ingredients, ingredients)
            set(it.vegan, vegan)
            set(it.description, description)
            where {
                it.id eq dish.id
            }
        }
    }

    fun list() : List<Dish> {
        return db.from(Dishes).select().map {
            Dish(
                it.getInt(1),
                it.getString(2)!!,
                it.getInt(3),
                it.getString(4)!!,
                it.getBoolean(5),
                it.getString(6)!!,
            )
        }
    }
}