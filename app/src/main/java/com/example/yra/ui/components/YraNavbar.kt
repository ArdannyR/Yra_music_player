package com.example.yra.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.yra.ui.navigation.YraDestinations

@Composable
fun YraNavbar(
    currentRoute: String,
    onNavigateToRoute: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Lobby") },
            label = { Text("Lobby") },
            selected = currentRoute == YraDestinations.LOBBY_ROUTE,
            onClick = { onNavigateToRoute(YraDestinations.LOBBY_ROUTE) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.MusicNote, contentDescription = "Songs") },
            label = { Text("Songs") },
            selected = currentRoute == YraDestinations.SONGS_ROUTE,
            onClick = { onNavigateToRoute(YraDestinations.SONGS_ROUTE) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Playlists") },
            label = { Text("Playlists") },
            selected = currentRoute == YraDestinations.PLAYLISTS_ROUTE,
            onClick = { onNavigateToRoute(YraDestinations.PLAYLISTS_ROUTE) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.StackedBarChart, contentDescription = "Stats") },
            label = { Text("Stats") },
            selected = currentRoute == YraDestinations.STATS_ROUTE,
            onClick = { onNavigateToRoute(YraDestinations.STATS_ROUTE) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Options") },
            label = { Text("Options") },
            selected = currentRoute == YraDestinations.OPTIONS_ROUTE,
            onClick = { onNavigateToRoute(YraDestinations.OPTIONS_ROUTE) }
        )
    }
}
