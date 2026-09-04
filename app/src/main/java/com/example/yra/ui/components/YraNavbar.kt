package com.example.yra.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.yra.ui.navigation.YraDestinations

@Composable
fun YraNavbar(
    currentRoute: String,
    onNavigateToRoute: (String) -> Unit
) {
    NeuCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                contentDescription = "Lobby",
                isSelected = currentRoute == YraDestinations.LOBBY_ROUTE,
                onClick = { onNavigateToRoute(YraDestinations.LOBBY_ROUTE) }
            )
            NavItem(
                icon = Icons.Default.MusicNote,
                contentDescription = "Songs",
                isSelected = currentRoute == YraDestinations.SONGS_ROUTE,
                onClick = { onNavigateToRoute(YraDestinations.SONGS_ROUTE) }
            )
            NavItem(
                icon = Icons.Default.LibraryMusic,
                contentDescription = "Playlists",
                isSelected = currentRoute == YraDestinations.PLAYLISTS_ROUTE,
                onClick = { onNavigateToRoute(YraDestinations.PLAYLISTS_ROUTE) }
            )
            NavItem(
                icon = Icons.Default.StackedBarChart,
                contentDescription = "Stats",
                isSelected = currentRoute == YraDestinations.STATS_ROUTE,
                onClick = { onNavigateToRoute(YraDestinations.STATS_ROUTE) }
            )
            NavItem(
                icon = Icons.Default.Settings,
                contentDescription = "Options",
                isSelected = currentRoute == YraDestinations.OPTIONS_ROUTE,
                onClick = { onNavigateToRoute(YraDestinations.OPTIONS_ROUTE) }
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val isPressed = isSelected // Mantiene el botón hundido si está seleccionado
    
    NeuIconButton(
        onClick = onClick,
        elevation = 4.dp,
        iconTint = tint,
        forcePressed = isPressed
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}
