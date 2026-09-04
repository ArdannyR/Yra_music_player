package com.example.yra.ui.playlists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yra.ui.playlists.components.CreatePlaylistDialog
import com.example.yra.ui.playlists.components.PlaylistCard

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistsViewModel,
    onNavigateToDetail: (Long?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is PlaylistsUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is PlaylistsUiState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is PlaylistsUiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Tarjeta estática para crear playlist
                    item {
                        PlaylistCard(
                            title = "Nueva",
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Crear",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = { showCreateDialog = true }
                        )
                    }

                    // Tarjeta virtual de Favoritos
                    item {
                        PlaylistCard(
                            title = "Favoritas",
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Favoritas",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = { onNavigateToDetail(null) },
                            isSpecial = true
                        )
                    }

                    // Playlists desde BD
                    items(state.playlists, key = { it.playlistId }) { playlist ->
                        PlaylistCard(
                            title = playlist.name,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.QueueMusic,
                                    contentDescription = "Playlist",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = { onNavigateToDetail(playlist.playlistId) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                viewModel.createPlaylist(name)
                showCreateDialog = false
            }
        )
    }
}
