package uk.ac.aber.dcs.cs39440.mealbay.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private const val PREFERENCES = "Storage"

class Storage(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCES)
    }

    suspend fun getString(key: String): String? {
        val sharedPrefKey = stringPreferencesKey(key)
        return context.dataStore.data.first()[sharedPrefKey]
    }

    suspend fun saveString(value: String, key: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }
}