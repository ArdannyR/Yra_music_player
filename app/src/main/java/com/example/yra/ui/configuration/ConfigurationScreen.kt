package com.example.yra.ui.configuration

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yra.domain.repository.ThemeMode
import java.net.URLDecoder

@Composable
fun ConfigurationScreen(
    viewModel: ConfigurationViewModel
) {
    val preferences by viewModel.preferencesState.collectAsState()
    val context = LocalContext.current
    
    var dirToRemove by remember { mutableStateOf<String?>(null) }

    val dirPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            // Persist permission
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(it, takeFlags)
            
            viewModel.addScanDirectory(it.toString())
        }
    }

    if (dirToRemove != null) {
        AlertDialog(
            onDismissRequest = { dirToRemove = null },
            title = { Text("Remove Directory") },
            text = { Text("Are you sure you want to stop scanning this directory? All songs associated with this folder will be removed from your Yra library (but not from the physical device).") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeScanDirectory(dirToRemove!!)
                    dirToRemove = null
                }) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { dirToRemove = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Configuration",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            com.example.yra.ui.components.NeuCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    SectionTitle("Appearance")
                    
                    com.example.yra.ui.components.SettingsRow(
                        label = "Theme"
                    ) {
                        ThemeSelector(
                            currentTheme = preferences.themeMode,
                            onThemeSelected = { viewModel.updateThemeMode(it) }
                        )
                    }
                    
                    com.example.yra.ui.components.SettingsRow(
                        label = "Font Size"
                    ) {
                        FontScaleSelector(
                            currentScale = preferences.fontScale,
                            onScaleSelected = { viewModel.updateFontScale(it) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            com.example.yra.ui.components.NeuCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionTitle("Music Library")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Scanned Directories",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "If the list is empty, the entire device will be scanned.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(preferences.scanDirectories.toList()) { uriString ->
            DirectoryItem(
                uriString = uriString,
                onRemove = { dirToRemove = uriString }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            com.example.yra.ui.components.NeuButton(
                onClick = { dirPickerLauncher.launch(null) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Directory", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ThemeSelector(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ThemeOption(
            text = "Sys",
            selected = currentTheme == ThemeMode.SYSTEM,
            onClick = { onThemeSelected(ThemeMode.SYSTEM) }
        )
        ThemeOption(
            text = "Light",
            selected = currentTheme == ThemeMode.LIGHT,
            onClick = { onThemeSelected(ThemeMode.LIGHT) }
        )
        ThemeOption(
            text = "Dark",
            selected = currentTheme == ThemeMode.DARK,
            onClick = { onThemeSelected(ThemeMode.DARK) }
        )
    }
}

@Composable
private fun ThemeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary

    com.example.yra.ui.components.NeuButton(
        onClick = onClick,
        backgroundColor = backgroundColor,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun FontScaleSelector(
    currentScale: Float,
    onScaleSelected: (Float) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ThemeOption(
            text = "S",
            selected = currentScale == 0.85f,
            onClick = { onScaleSelected(0.85f) }
        )
        ThemeOption(
            text = "M",
            selected = currentScale == 1.0f,
            onClick = { onScaleSelected(1.0f) }
        )
        ThemeOption(
            text = "L",
            selected = currentScale == 1.15f,
            onClick = { onScaleSelected(1.15f) }
        )
    }
}

@Composable
private fun DirectoryItem(
    uriString: String,
    onRemove: () -> Unit
) {
    val decodedPath = try {
        URLDecoder.decode(uriString.substringAfterLast("/"), "UTF-8")
    } catch (e: Exception) {
        uriString
    }
    
    com.example.yra.ui.components.NeuCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = decodedPath,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            com.example.yra.ui.components.NeuIconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Directory",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
