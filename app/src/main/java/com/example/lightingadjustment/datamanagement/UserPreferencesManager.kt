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
class UserPreferencesManager(context: Context) {
    private val dataStore = context.userPreferencesDataStore

    suspend fun initialize() {
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setBrightness(0.0f) // 设置默认值
                .build()
        }
    }

    // Use one or multiple String field(s) to get the data of Map type in preferences
    suspend fun getUserPreferences(vararg fields: String): Map<String, Any?> {
        val preferences = dataStore.data.first()
        return fields.mapNotNull { field ->
            when (field) {
                "initialized" -> "initialized" to preferences.initialized
                "brightness" -> "brightness" to preferences.brightness
                "color" -> "color" to preferences.color
                "sceneMode" -> "sceneMode" to preferences.sceneMode
                "operationMode" -> "operationMode" to preferences.operationMode
                else -> null
            }
        }.toMap()
    }

    // Update the data in preferences, which can be optionally filled in
    suspend fun updateUserPreferences(initialized: Boolean? = null,
                                      brightness: Float? = null,
                                      color: String? = null,
                                      sceneMode: String? = null,
                                      operationMode: String? = null) {
        dataStore.updateData { preferences ->
            preferences.toBuilder().apply {
                initialized?.let{ setInitialized(initialized) }
                brightness?.let{ setBrightness(brightness) }
                color?.let { setColor(color) }
                sceneMode?. let{ setSceneMode(sceneMode) }
                operationMode?.let{ setOperationMode(operationMode) }
            }.build()
        }
    }

    // Check if the data has been initialized
    suspend fun isInitialized(): Boolean {
        return getUserPreferences("initialized")["initialized"] as Boolean
    }
}