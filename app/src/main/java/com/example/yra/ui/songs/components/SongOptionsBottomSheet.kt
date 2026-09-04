package com.example.yra.ui.songs.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.yra.data.local.SongEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongOptionsBottomSheet(
    song: SongEntity,
    onDismiss: () -> Unit,
    onEditDataClick: () -> Unit,
    onDetailedDataClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onGoToOptionsClick: () -> Unit,
    onShareClick: () -> Unit,
    onGoToFolderClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            BottomSheetItem(
                icon = Icons.Default.PlaylistAdd,
                text = "Añadir a Playlist",
                onClick = {
                    onDismiss()
                    onAddToPlaylistClick()
                }
            )
            BottomSheetItem(
                icon = Icons.Default.Edit,
                text = "Editar Metadatos",
                onClick = {
                    onDismiss()
                    onEditDataClick()
                }
            )
            BottomSheetItem(
                icon = Icons.Default.Info,
                text = "Detalles Técnicos",
                onClick = {
                    onDismiss()
                    onDetailedDataClick()
                }
            )
            BottomSheetItem(
                icon = Icons.Default.Settings,
                text = "Opciones",
                onClick = {
                    onDismiss()
                    onGoToOptionsClick()
                }
            )
            BottomSheetItem(
                icon = Icons.Default.Share,
                text = "Compartir",
                onClick = {
                    onDismiss()
                    onShareClick()
                }
            )
            BottomSheetItem(
                icon = Icons.Default.Folder,
                text = "Ir a la carpeta",
                onClick = {
                    onDismiss()
                    onGoToFolderClick()
                }
            )
            BottomSheetItem(
                icon = Icons.Default.Delete,
                text = "Eliminar del dispositivo",
                tint = MaterialTheme.colorScheme.error,
                onClick = {
                    onDismiss()
                    onDeleteClick()
                }
            )
        }
    }
}

@Composable
private fun BottomSheetItem(
    icon: ImageVector,
    text: String,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (tint == MaterialTheme.colorScheme.error) tint else MaterialTheme.colorScheme.onSurface
        )
    }
}
