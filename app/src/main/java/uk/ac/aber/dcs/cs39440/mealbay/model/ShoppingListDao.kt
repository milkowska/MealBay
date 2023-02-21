package uk.ac.aber.dcs.cs39440.mealbay.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShoppingListDao {

    @Insert
    suspend fun insertItem(item: ShoppingListItem)

    @Delete
    suspend fun deleteItem(item: ShoppingListItem)

    @Query("SELECT * FROM shopping_list")
    fun getAllItems(): LiveData<List<ShoppingListItem>>


    @Query("DELETE FROM shopping_list")
    suspend fun clearShoppingList()
}