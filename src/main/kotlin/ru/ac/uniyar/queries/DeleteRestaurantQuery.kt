package ru.ac.uniyar.queries

import ru.ac.uniyar.domain.Store
import java.util.*

class DeleteRestaurantQuery (private val store: Store) {
    operator fun invoke(id:UUID){
        store.restaurantRepository.delete(id)
        store.save()
    }
}