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

    suspend fun getStringList(key: String): List<String>? {
        val sharedPrefKey = stringPreferencesKey(key)
        return context.dataStore.data.first()[sharedPrefKey]?.split(",")?.map { it.trim() }
    }

    suspend fun saveStringList(list: List<String>, key: String) {
        val joinedString = list.joinToString(separator = "|") // Use "|" as a delimiter
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = joinedString
        }
    }

    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val booleanKey = booleanPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[booleanKey] ?: defaultValue
    }
    suspend fun saveBoolean(key: String, value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }




}