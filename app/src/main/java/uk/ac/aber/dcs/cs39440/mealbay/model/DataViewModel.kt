package uk.ac.aber.dcs.cs39440.mealbay.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.ac.aber.dcs.cs39440.mealbay.storage.Storage
import javax.inject.Inject


@HiltViewModel
class DataViewModel @Inject constructor(
    private val storage: Storage
) : ViewModel() {

    fun getString(key: String): String? = runBlocking {
        storage.getString(key)
    }

    fun saveString(value: String, key: String) {
        viewModelScope.launch {
            storage.saveString(value, key)
        }
    }

    fun getStringList(key: String): List<String>? = runBlocking {
        storage.getStringList(key)
    }

    fun saveStringList(list: List<String>, key: String) {
        viewModelScope.launch {
            storage.saveStringList(list, key)
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean = runBlocking {
        storage.getBoolean(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) {
        viewModelScope.launch {
            storage.saveBoolean(key, value)
        }
    }

}
