package uk.ac.aber.dcs.cs39440.mealbay.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.ac.aber.dcs.cs39440.mealbay.storage.Storage
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


@HiltViewModel
class DataViewModel @Inject constructor(
    private val storage: Storage
) : ViewModel() {

    private var userCollectionListener: ListenerRegistration? = null

    private val _isUserCollectionEmpty = MutableLiveData<Boolean>()
    val isUserCollectionEmpty: LiveData<Boolean> = _isUserCollectionEmpty

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

    fun checkUserCollectionEmpty(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val userCollectionRef = db.collection("users").document(userId).collection("collections")

        // Remove any existing listener
        userCollectionListener?.remove()

        // Set up a new listener
        userCollectionListener = userCollectionRef.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                Log.e("DataViewModel", "Error checking user collection: ", exception)
                return@addSnapshotListener
            }
            _isUserCollectionEmpty.value = querySnapshot?.isEmpty ?: true
        }
    }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        userCollectionListener?.remove()
    }


}
