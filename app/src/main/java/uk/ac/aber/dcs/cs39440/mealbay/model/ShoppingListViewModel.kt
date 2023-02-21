package uk.ac.aber.dcs.cs39440.mealbay.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs39440.mealbay.datasource.MealBayRepository

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MealBayRepository = MealBayRepository(application)

    var shoppingList: LiveData<List<ShoppingListItem>> = loadList()
            private set

    fun insertShoppingListItem(shoppingListItem: ShoppingListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(shoppingListItem)
        }
    }

    fun clearShoppingList() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearShoppingList()
        }
    }

    fun deleteShoppingListItem(shoppingListItem: ShoppingListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(shoppingListItem)
        }
    }

    private fun loadList(): LiveData<List<ShoppingListItem>> {
        return repository.getShoppingList()
    }

}