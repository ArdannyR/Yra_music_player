package com.example.yra.ui.configuration

import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.yra.domain.repository.SongRepository
import com.example.yra.domain.repository.ThemeMode
import com.example.yra.domain.repository.UserPreferences
import com.example.yra.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConfigurationViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    val preferencesState: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.updateThemeMode(themeMode)
        }
    }

    fun updateFontScale(scale: Float) {
        viewModelScope.launch {
            userPreferencesRepository.updateFontScale(scale)
        }
    }

    fun addScanDirectory(uriString: String) {
        viewModelScope.launch {
            userPreferencesRepository.addScanDirectory(uriString)
            // Rescan MediaStore with the new settings
            val currentPrefs = preferencesState.value
            val updatedDirs = currentPrefs.scanDirectories + uriString
            songRepository.syncWithMediaStore(updatedDirs)
        }
    }

    fun removeScanDirectory(uriString: String) {
        viewModelScope.launch {
            // Convert SAF URI to path prefix to delete songs
            val pathPrefix = getPathFromTreeUri(Uri.parse(uriString))
            if (pathPrefix != null) {
                songRepository.deleteSongsByPathPrefix(pathPrefix)
            }
            
            userPreferencesRepository.removeScanDirectory(uriString)
            // Rescan with updated settings
            val currentPrefs = preferencesState.value
            val updatedDirs = currentPrefs.scanDirectories - uriString
            songRepository.syncWithMediaStore(updatedDirs)
        }
    }

    private fun getPathFromTreeUri(uri: Uri): String? {
        val path = uri.path ?: return null
        val docId = path.substringAfter("/tree/")
        val split = docId.split(":")
        if (split.size >= 2) {
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        }
        return null
    }
}

class ConfigurationViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val songRepository: SongRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfigurationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConfigurationViewModel(userPreferencesRepository, songRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
