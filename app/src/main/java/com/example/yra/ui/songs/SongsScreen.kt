package com.example.yra.ui.songs

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yra.data.local.SongEntity
import com.example.yra.domain.playback.PlaybackController
import com.example.yra.ui.playlists.PlaylistsUiState
import com.example.yra.ui.playlists.PlaylistsViewModel
import com.example.yra.ui.playlists.components.AddToPlaylistDialog
import com.example.yra.ui.songs.components.EditSongDialog
import com.example.yra.ui.songs.components.SongCard
import com.example.yra.ui.songs.components.SongDetailedDataDialog
import com.example.yra.ui.songs.components.SongOptionsBottomSheet
import android.content.Context
import android.provider.MediaStore
import android.net.Uri
import androidx.activity.result.IntentSenderRequest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.yra.R

@Composable
fun SongsScreen(
    viewModel: SongsViewModel,
    playbackController: PlaybackController,
    playlistsViewModel: PlaylistsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val playlistsUiState by playlistsViewModel.uiState.collectAsState()
    var songToEdit by remember { mutableStateOf<SongEntity?>(null) }
    var songToAddToPlaylist by remember { mutableStateOf<SongEntity?>(null) }
    var songOptions by remember { mutableStateOf<SongEntity?>(null) }
    var songDetailedData by remember { mutableStateOf<SongEntity?>(null) }
    var songToDelete by remember { mutableStateOf<SongEntity?>(null) }
    
    val context = LocalContext.current

    // MediaStore Delete Launcher
    val deleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // The file was deleted by the system, now remove it from Room
            songToDelete?.let { song ->
                viewModel.deleteSongFromDb(song.id)
                songToDelete = null
            }
        } else {
            // User denied or it failed, just close the dialog state
            songToDelete = null
        }
    }
    
    // Permission handling
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.syncMediaStore()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(permission)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is SongsUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is SongsUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.syncMediaStore() }, modifier = Modifier.padding(top = 8.dp)) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
            }
            is SongsUiState.Success -> {
                if (state.songs.isEmpty()) {
                    Text(
                        stringResource(R.string.no_songs_found),
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.songs, key = { it.id }) { song ->
                            SongCard(
                                song = song,
                                onClick = { playbackController.playSong(song) },
                                onEditClick = { songToEdit = song },
                                onAddToPlaylistClick = { songToAddToPlaylist = song },
                                onToggleFavorite = { viewModel.toggleFavorite(song) },
                                onDeleteClick = { songToDelete = song },
                                onOptionsClick = { songOptions = song }
                            )
                        }
                    }
                }
            }
        }
    }

    songToEdit?.let { song ->
        EditSongDialog(
            song = song,
            onDismiss = { songToEdit = null },
            onSave = { updatedSong ->
                viewModel.updateSong(updatedSong)
                songToEdit = null
            }
        )
    }

    songToAddToPlaylist?.let { song ->
        if (playlistsUiState is PlaylistsUiState.Success) {
            val playlists = (playlistsUiState as PlaylistsUiState.Success).playlists
            AddToPlaylistDialog(
                playlists = playlists,
                onDismiss = { songToAddToPlaylist = null },
                onPlaylistSelected = { playlistId ->
                    playlistsViewModel.addSongToPlaylist(playlistId, song.id)
                    songToAddToPlaylist = null
                }
            )
        }
    }

    songDetailedData?.let { song ->
        SongDetailedDataDialog(
            song = song,
            onDismiss = { songDetailedData = null }
        )
    }

    songToDelete?.let { song ->
        AlertDialog(
            onDismissRequest = { songToDelete = null },
            title = { Text(stringResource(R.string.dialog_delete_song_title)) },
            text = { Text(stringResource(R.string.dialog_delete_song_message)) },
            confirmButton = {
                TextButton(onClick = {
                    try {
                        val uri = Uri.parse(song.uri)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val pendingIntent = MediaStore.createDeleteRequest(context.contentResolver, listOf(uri))
                            deleteLauncher.launch(IntentSenderRequest.Builder(pendingIntent).build())
                        } else {
                            // Pre-Android 11 physical deletion
                            val rowsDeleted = context.contentResolver.delete(uri, null, null)
                            if (rowsDeleted > 0) {
                                viewModel.deleteSongFromDb(song.id)
                            }
                            songToDelete = null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        songToDelete = null
                    }
                }) {
                    Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { songToDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    songOptions?.let { song ->
        SongOptionsBottomSheet(
            song = song,
            onDismiss = { songOptions = null },
            onEditDataClick = { songToEdit = song },
            onDetailedDataClick = { songDetailedData = song },
            onAddToPlaylistClick = { songToAddToPlaylist = song },
            onGoToOptionsClick = {
                // TODO: Navigate to global options
            },
            onShareClick = {
                // TODO: Share Intent
            },
            onGoToFolderClick = {
                // TODO: Go to folder logic
            },
            onDeleteClick = { songToDelete = song }
        )
    }
}
