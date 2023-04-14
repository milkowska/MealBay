package uk.ac.aber.dcs.cs39440.mealbay.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private const val PREFERENCES = "Storage"

/**
 * The Storage class manages the storage of data in the application, using the Context object provided to it.
 */
class Storage(private val context: Context) {

    // Defines an extension property on the Context object which provides access to a DataStore object for storing and retrieving key-value pairs of preferences.
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCES)
    }

    /**
     * getString function retrieves the String value associated with the given key from the data store asynchronously.
     * @param key The key to retrieve the String value for.
     * @return The String value associated with the given key, or null if the key is not found.
     */
    suspend fun getString(key: String): String? {
        val sharedPrefKey = stringPreferencesKey(key)
        return context.dataStore.data.first()[sharedPrefKey]
    }

    /**
     * saveString function is used to save a string value to Android's DataStore. The function is executed asynchronously
     * using coroutines without blocking the main thread.
     * @param value a string value to be saved.
     * @param key which is used to retrieve the value later on.
     */
    suspend fun saveString(value: String, key: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    /**
     * getStringList function retrieves the String list associated with the given key from the data store asynchronously.
     * @param key The key to retrieve the String value for.
     * @return The String list associated with the given key, or null if the key is not found.
     */
    suspend fun getStringList(key: String): List<String>? {
        val sharedPrefKey = stringPreferencesKey(key)
        return context.dataStore.data.first()[sharedPrefKey]?.split(",")?.map { it.trim() }
    }

    /**
     * saveStringList function saves a list of strings as a single string in the data store with the given key. The strings
     * in the list are joined with "|" as a delimiter before being stored.
     * @param list The list of strings to be saved.
     * @param key The key to associate with the saved string in the data store.
     */
    suspend fun saveStringList(list: List<String>, key: String) {
        val joinedString = list.joinToString(separator = "|") // Use "|" as a delimiter
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = joinedString
        }
    }
}