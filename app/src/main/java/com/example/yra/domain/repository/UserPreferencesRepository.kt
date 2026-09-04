package com.example.yra.domain.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeMode(val value: Int) {
    SYSTEM(0),
    LIGHT(1),
    DARK(2);

    companion object {
        fun fromValue(value: Int) = values().firstOrNull { it.value == value } ?: SYSTEM
    }
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val fontScale: Float = 1.0f,
    val scanDirectories: Set<String> = emptySet()
)

class UserPreferencesRepository(private val context: Context) {

    private val THEME_MODE = intPreferencesKey("theme_mode")
    private val FONT_SCALE = floatPreferencesKey("font_scale")
    private val SCAN_DIRECTORIES = stringSetPreferencesKey("scan_directories")

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val themeModeValue = preferences[THEME_MODE] ?: ThemeMode.SYSTEM.value
        val fontScale = preferences[FONT_SCALE] ?: 1.0f
        val scanDirectories = preferences[SCAN_DIRECTORIES] ?: emptySet()
        
        UserPreferences(
            themeMode = ThemeMode.fromValue(themeModeValue),
            fontScale = fontScale,
            scanDirectories = scanDirectories
        )
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = themeMode.value
        }
    }

    suspend fun updateFontScale(fontScale: Float) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SCALE] = fontScale
        }
    }

    suspend fun addScanDirectory(uriString: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[SCAN_DIRECTORIES] ?: emptySet()
            preferences[SCAN_DIRECTORIES] = current + uriString
        }
    }

    suspend fun removeScanDirectory(uriString: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[SCAN_DIRECTORIES] ?: emptySet()
            preferences[SCAN_DIRECTORIES] = current - uriString
        }
    }
}
