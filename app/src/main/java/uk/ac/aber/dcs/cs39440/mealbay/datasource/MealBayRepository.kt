package uk.ac.aber.dcs.cs39440.mealbay.datasource

import android.app.Application
import uk.ac.aber.dcs.cs39440.mealbay.model.ShoppingListItem

class MealBayRepository(application: Application) {
    private val shoppingListDao = MealBayRoomDatabase.getDatabase(application)!!.shoppingListDao()

    suspend fun insert(shoppingListItem: ShoppingListItem) {
        shoppingListDao.insertItem(shoppingListItem)
    }

    suspend fun clearShoppingList() {
        shoppingListDao.clearShoppingList()
    }

    suspend fun delete(shoppingListItem: ShoppingListItem) {
        shoppingListDao.deleteItem(shoppingListItem)
    }

    fun getShoppingList() = shoppingListDao.getAllItems()

}