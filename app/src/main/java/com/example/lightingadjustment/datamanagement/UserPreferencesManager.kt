package com.example.lightingadjustment.datamanagement

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.example.lightingadjustment.proto.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File

// Get corresponding data store.
private val dataStoreCache = mutableMapOf<String, DataStore<UserPreferences>>()

fun Context.userPreferencesDataStore(fileName: String): DataStore<UserPreferences> {
    return dataStoreCache.getOrPut(fileName) {
        DataStoreFactory.create (
            produceFile = { File(this.filesDir, fileName) },
            serializer = UserPreferencesSerializer
        )
    }
}

// Get corresponding preference.
object UserPreferencesRepository {
    private val managerMap = mutableMapOf<String, UserPreferencesManager>()

    fun getManager(context: Context, fileName: String): UserPreferencesManager {
        return managerMap.getOrPut(fileName) {
            UserPreferencesManager(context.applicationContext, fileName)
        }
    }
    /*
    fun clearAll() {
        managerMap.clear()
    }
    */
}

// Data extracted from the corresponding data store.
data class TempUserPreferences(
    val brightness: Float,
    val color: String,
    val sceneMode: String,
    val operationMode: String,
    val part: Boolean
)

// A class convenient for data management
class UserPreferencesManager(context: Context, val fileName: String) {
    private val dataStore = context.userPreferencesDataStore(fileName)

    suspend fun initialize() {
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setBrightness(2.0f)
                .setColor("white")
                .setOperationMode("manualMode")
                .setInitialized(true)
                .setPart(false)
                .setSceneMode("Sleeping")
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
                "part" -> "part" to preferences.part
                else -> null
            }
        }.toMap()
    }

    // Update the data in preferences, which can be optionally filled in
    suspend fun updateUserPreferences(initialized: Boolean? = null,
                                      brightness: Float? = null,
                                      color: String? = null,
                                      sceneMode: String? = null,
                                      operationMode: String? = null,
                                      part: Boolean?= null) {
        dataStore.updateData { preferences ->
            preferences.toBuilder().apply {
                initialized?.let{ setInitialized(initialized) }
                brightness?.let{ setBrightness(brightness) }
                color?.let { setColor(color) }
                sceneMode?. let{ setSceneMode(sceneMode) }
                operationMode?.let{ setOperationMode(operationMode) }
                part?.let{ setPart(part) }
            }.build()
        }
    }

    // Check if the data has been initialized
    suspend fun isInitialized(): Boolean {
        return getUserPreferences("initialized")["initialized"] as Boolean
    }

    // Monitor the data displayed on pages.
    val preferencesFlow: Flow<TempUserPreferences> = dataStore.data
        .map { prefs ->
            TempUserPreferences(
                brightness = prefs.brightness,
                color = prefs.color ?: "white",
                sceneMode = prefs.sceneMode ?: "Sleeping",
                operationMode = prefs.operationMode ?: "manualMode",
                part = prefs.part
            )
        }
}