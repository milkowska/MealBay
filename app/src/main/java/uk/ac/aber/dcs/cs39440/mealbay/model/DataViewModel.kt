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

/**
 * This is a ViewModel class constructed using Hilt dependency injection to handle saving and retrieving recipe data
 * while moving through screens.
 */
@HiltViewModel
class DataViewModel @Inject constructor(
    private val storage: Storage
) : ViewModel() {

    private var userCollectionListener: ListenerRegistration? = null

    private val _isUserCollectionEmpty = MutableLiveData<Boolean>()
    val isUserCollectionEmpty: LiveData<Boolean> = _isUserCollectionEmpty

    /**
     * getString function retrieves a string from the storage with the specified key.
     * @param key The key associated with the string in the storage.
     *
     * @return the String value associated with the key, or null if not found in storage.
     */
    fun getString(key: String): String? = runBlocking {
        storage.getString(key)
    }

    /**
     * saveString function saves a string to the storage using a key.
     * @param value The string value to be saved.
     * @param key The key to associate with the string value.
     */
    fun saveString(value: String, key: String) {
        viewModelScope.launch {
            storage.saveString(value, key)
        }
    }

    /**
     *  getStringList function retrieves a string list from the storage with the specified key.
     *  @param key The key associated with the string list in the storage.
     *
     *  @return The string list associated with the key, or null if no value is found.
     *  */
    fun getStringList(key: String): List<String>? = runBlocking {
        storage.getStringList(key)
    }

    /**
     * saveStringList function saves a list of strings to the storage using a key.
     * @param list The list of strings to be saved.
     * @param key The key to associate with the list.
     */
    fun saveStringList(list: List<String>, key: String) {
        viewModelScope.launch {
            storage.saveStringList(list, key)
        }
    }

    /**
     * checkUserCollectionEmpty function checks if a user's collection is empty by listening for changes to the collection in Firestore.
     * @param userId the current user ID whose collection is to be checked.
     */
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
     * This function will be called when this ViewModel is no longer used and will be destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        userCollectionListener?.remove()
    }
}
