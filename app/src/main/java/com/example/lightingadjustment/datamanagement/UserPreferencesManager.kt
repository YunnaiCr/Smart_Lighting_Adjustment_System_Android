package com.example.lightingadjustment.datamanagement

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.lightingadjustment.proto.UserPreferences
import kotlinx.coroutines.flow.first

private val Context.userPreferencesDataStore: DataStore<UserPreferences> by dataStore(
    fileName = "user_preferences.pb",
    serializer = UserPreferencesSerializer
)

// A class convenient for data management
class UserPreferencesManager(private val context: Context) {
    private val dataStore = context.userPreferencesDataStore

    // Use one or multiple String field(s) to get the data of Map type in preferences
    suspend fun getUserPreferences(vararg fields: String): Map<String, Any?> {
        val preferences = dataStore.data.first()
        return fields.mapNotNull { field ->
            when (field) {
                "initialized" -> "initialized" to preferences.initialized
                "adjustments" -> "adjustments" to preferences.adjustments
                "options" -> "options" to preferences.options
                else -> null
            }
        }.toMap()
    }

    // Update the data in preferences, which can be optionally filled in
    suspend fun updateUserPreferences(initialized: Boolean? = null, adjustments: String? = null, options: String? = null) {
        dataStore.updateData { preferences ->
            preferences.toBuilder().apply {
                initialized?.let{setInitialized(initialized)}
                adjustments?.let{setAdjustments(adjustments)}
                options?.let{setOptions(options)}
            }.build()
        }
    }

    // Check if the data has been initialized
    suspend fun isInitialized(): Boolean {
        return getUserPreferences("initialized")["initialized"] as Boolean
    }
}