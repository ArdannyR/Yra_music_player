package com.example.yra.ui.playlists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yra.domain.playback.PlaybackController
import com.example.yra.ui.songs.components.SongCard

@Composable
fun PlaylistDetailScreen(
    playlistId: Long?, // null means Favorites
    viewModel: PlaylistsViewModel,
    playbackController: PlaybackController
) {
    val detailState by viewModel.detailUiState.collectAsState()

    LaunchedEffect(playlistId) {
        viewModel.loadPlaylistDetails(playlistId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = detailState) {
            is PlaylistDetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is PlaylistDetailUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is PlaylistDetailUiState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (state.songs.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Esta lista está vacía.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.songs, key = { it.id }) { song ->
                                SongCard(
                                    song = song,
                                    onClick = { playbackController.playSong(song) },
                                    onEditClick = { /* Opcional en detalle */ },
                                    onAddToPlaylistClick = { /* Opcional en detalle */ },
                                    onToggleFavorite = { /* Para simplicidad en esta vista no conectamos la edición directamente */ },
                                    onDeleteClick = { /* Opcional en detalle */ },
                                    onOptionsClick = { /* TODO: abrir Song_options BottomSheet para esta canción */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
