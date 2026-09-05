package com.example.yra.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yra.ui.components.NeuCard
import com.example.yra.ui.songs.components.SongCard

@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    onSongClick: (com.example.yra.data.local.SongEntity) -> Unit
) {
    val totalTimeToday by viewModel.totalTimeListenedToday.collectAsState()
    val totalTimeWeek by viewModel.totalTimeListenedWeek.collectAsState()
    val totalTimeHistorical by viewModel.totalHistoricalListeningTime.collectAsState()
    val mostPlayedSongs by viewModel.mostPlayedSongs.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {

        item {
            NeuCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Listening Time", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    TimeRow("Today", formatTime(totalTimeToday))
                    TimeRow("This Week", formatTime(totalTimeWeek))
                    TimeRow("All Time", formatTime(totalTimeHistorical))
                }
            }
        }

        item {
            Text(
                text = "Most Played",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        items(mostPlayedSongs) { song ->
            SongCard(
                song = song,
                onClick = { onSongClick(song) },
                onEditClick = { /* No aplica en esta vista de solo lectura */ },
                onAddToPlaylistClick = { /* No aplica en esta vista de solo lectura */ },
                onToggleFavorite = { /* TODO: conectar si quieres marcar favorito desde Stats */ },
                onDeleteClick = { /* No aplica en esta vista de solo lectura */ },
                onOptionsClick = { /* TODO if needed in stats */ }
            )
        }
        
        if (mostPlayedSongs.isEmpty()) {
            item {
                Text(
                    text = "No listening history yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun TimeRow(label: String, timeStr: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = timeStr, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}
