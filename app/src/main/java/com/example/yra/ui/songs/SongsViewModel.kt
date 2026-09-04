package com.example.yra.ui.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.yra.data.local.SongEntity
import com.example.yra.domain.repository.SongRepository
import com.example.yra.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class SongsUiState {
    object Loading : SongsUiState()
    data class Success(val songs: List<SongEntity>) : SongsUiState()
    data class Error(val message: String) : SongsUiState()
}

class SongsViewModel(
    private val repository: SongRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SongsUiState>(SongsUiState.Loading)
    val uiState: StateFlow<SongsUiState> = _uiState.asStateFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            _uiState.value = SongsUiState.Loading
            repository.getAllSongs()
                .catch { e -> _uiState.value = SongsUiState.Error(e.message ?: "Unknown error") }
                .collect { songs ->
                    _uiState.value = SongsUiState.Success(songs)
                }
        }
    }

    fun syncMediaStore() {
        viewModelScope.launch {
            try {
                val prefs = userPreferencesRepository.userPreferencesFlow.first()
                repository.syncWithMediaStore(prefs.scanDirectories)
            } catch (e: Exception) {
                // If sync fails, the UI will still show local DB data.
                // We could emit a side effect here if needed.
            }
        }
    }

    fun updateSong(song: SongEntity) {
        viewModelScope.launch {
            repository.updateSong(song)
        }
    }

    fun deleteSongFromDb(id: Long) {
        viewModelScope.launch {
            repository.deleteSong(id)
        }
    }
    
    fun toggleFavorite(song: SongEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(song.id, !song.isFavorite)
        }
    }
}

class SongsViewModelFactory(
    private val repository: SongRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SongsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SongsViewModel(repository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
