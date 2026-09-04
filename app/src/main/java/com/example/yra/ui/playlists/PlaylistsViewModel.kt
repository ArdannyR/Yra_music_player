package com.example.yra.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.yra.data.local.PlaylistEntity
import com.example.yra.data.local.SongEntity
import com.example.yra.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class PlaylistsUiState {
    object Loading : PlaylistsUiState()
    data class Success(val playlists: List<PlaylistEntity>) : PlaylistsUiState()
    data class Error(val message: String) : PlaylistsUiState()
}

sealed class PlaylistDetailUiState {
    object Loading : PlaylistDetailUiState()
    data class Success(val title: String, val songs: List<SongEntity>) : PlaylistDetailUiState()
    data class Error(val message: String) : PlaylistDetailUiState()
}

class PlaylistsViewModel(
    private val repository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistsUiState>(PlaylistsUiState.Loading)
    val uiState: StateFlow<PlaylistsUiState> = _uiState.asStateFlow()

    private val _detailUiState = MutableStateFlow<PlaylistDetailUiState>(PlaylistDetailUiState.Loading)
    val detailUiState: StateFlow<PlaylistDetailUiState> = _detailUiState.asStateFlow()

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            _uiState.value = PlaylistsUiState.Loading
            repository.getAllPlaylists()
                .catch { e -> _uiState.value = PlaylistsUiState.Error(e.message ?: "Unknown error") }
                .collect { playlists ->
                    _uiState.value = PlaylistsUiState.Success(playlists)
                }
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            repository.createPlaylist(name)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun loadPlaylistDetails(playlistId: Long?) {
        viewModelScope.launch {
            _detailUiState.value = PlaylistDetailUiState.Loading
            if (playlistId == null) {
                // Load favorites
                repository.getFavoriteSongs()
                    .catch { e -> _detailUiState.value = PlaylistDetailUiState.Error(e.message ?: "Error") }
                    .collect { songs ->
                        _detailUiState.value = PlaylistDetailUiState.Success("Favoritas", songs)
                    }
            } else {
                repository.getPlaylistWithSongs(playlistId)
                    .catch { e -> _detailUiState.value = PlaylistDetailUiState.Error(e.message ?: "Error") }
                    .collect { playlistWithSongs ->
                        if (playlistWithSongs != null) {
                            _detailUiState.value = PlaylistDetailUiState.Success(
                                playlistWithSongs.playlist.name,
                                playlistWithSongs.songs
                            )
                        } else {
                            _detailUiState.value = PlaylistDetailUiState.Error("Playlist no encontrada")
                        }
                    }
            }
        }
    }
}

class PlaylistsViewModelFactory(private val repository: PlaylistRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaylistsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
