package com.example.yra.ui.options

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.yra.domain.playback.SleepTimerState
import com.example.yra.ui.components.NeuCard
import com.example.yra.ui.components.NeuButton
import com.example.yra.ui.components.CircularDurationPicker

@Composable
fun OptionsScreen(viewModel: OptionsViewModel) {
    val eqEnabled by viewModel.equalizerManager.isEqualizerEnabled.collectAsState()
    val bands by viewModel.equalizerManager.bands.collectAsState()
    val timerState by viewModel.sleepTimerManager.timerState.collectAsState()
    
    var dialMinutes by remember { mutableIntStateOf(15) }
    var exactMinutes by remember { mutableStateOf("") }
    var exactSongs by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Sleep Timer Section
        item {
            NeuCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "Sleep Timer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    
                    Text(
                        text = when (val state = timerState) {
                            is SleepTimerState.Idle -> "No timer set"
                            is SleepTimerState.TimeBased -> "Time remaining: ${state.remainingSeconds / 60}m ${state.remainingSeconds % 60}s"
                            is SleepTimerState.TrackBased -> "Tracks remaining: ${state.remainingTracks}"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    CircularDurationPicker(
                        currentMinutes = dialMinutes,
                        onMinutesChanged = { dialMinutes = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    NeuButton(
                        onClick = { 
                            if (dialMinutes > 0) {
                                viewModel.setTimer(dialMinutes, null)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 8.dp)
                    ) {
                        Text("Start Timer ($dialMinutes min)", style = MaterialTheme.typography.labelLarge)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = exactMinutes,
                            onValueChange = { exactMinutes = it.filter { char -> char.isDigit() } },
                            label = { Text("Exact minutes") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        NeuButton(
                            onClick = { 
                                val min = exactMinutes.toIntOrNull()
                                if (min != null && min > 0) {
                                    viewModel.setTimer(min, null)
                                    exactMinutes = ""
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text("Set")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = exactSongs,
                            onValueChange = { exactSongs = it.filter { char -> char.isDigit() } },
                            label = { Text("Exact song count") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        NeuButton(
                            onClick = { 
                                val count = exactSongs.toIntOrNull()
                                if (count != null && count > 0) {
                                    viewModel.setTimer(null, count)
                                    exactSongs = ""
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text("Set")
                        }
                    }
                    
                    if (timerState !is SleepTimerState.Idle) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = { viewModel.setTimer(null, null) },
                            modifier = Modifier.align(Alignment.End).padding(horizontal = 8.dp)
                        ) {
                            Text("Cancel Timer", color = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Equalizer Section
        item {
            NeuCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "Audio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    
                    com.example.yra.ui.components.SettingsRow(
                        label = "Equalizer"
                    ) {
                        Switch(
                            checked = eqEnabled,
                            onCheckedChange = { viewModel.equalizerManager.setEqualizerEnabled(it) }
                        )
                    }

                    if (eqEnabled && bands.isNotEmpty()) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            bands.forEach { band ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (band.centerFreqHz >= 1000) "${band.centerFreqHz / 1000}kHz" else "${band.centerFreqHz}Hz",
                                        modifier = Modifier.width(50.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Slider(
                                        value = band.currentLevel.toFloat(),
                                        onValueChange = { 
                                            viewModel.equalizerManager.setBandLevel(band.bandIndex, it.toInt().toShort()) 
                                        },
                                        valueRange = band.minLevel.toFloat()..band.maxLevel.toFloat(),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    } else if (eqEnabled && bands.isEmpty()) {
                        Text(
                            text = "Equalizer not available on this device or no active audio session.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
