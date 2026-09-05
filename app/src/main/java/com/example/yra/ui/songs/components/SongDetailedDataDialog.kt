package com.example.yra.ui.songs.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.yra.R
import com.example.yra.data.local.SongEntity
import java.io.File
import java.util.concurrent.TimeUnit

@Composable
fun SongDetailedDataDialog(
    song: SongEntity,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.song_details_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                DetailItem(label = stringResource(R.string.song_details_filename), value = song.path.substringAfterLast('/'))
                Spacer(modifier = Modifier.height(8.dp))

                DetailItem(label = stringResource(R.string.song_details_path), value = song.path)
                Spacer(modifier = Modifier.height(8.dp))

                DetailItem(label = stringResource(R.string.song_details_uri), value = song.uri)
                Spacer(modifier = Modifier.height(8.dp))

                val sizeInMb = File(song.path).length() / 1024 / 1024
                DetailItem(label = stringResource(R.string.song_details_size), value = "$sizeInMb MB")
                Spacer(modifier = Modifier.height(8.dp))

                DetailItem(label = stringResource(R.string.song_details_duration), value = formatDuration(song.duration))
                Spacer(modifier = Modifier.height(8.dp))
                
                DetailItem(label = stringResource(R.string.song_details_id), value = song.id.toString())
                Spacer(modifier = Modifier.height(24.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.action_close))
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatDuration(durationMs: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, seconds)
}
