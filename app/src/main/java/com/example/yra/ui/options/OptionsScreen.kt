package com.example.yra.ui.options

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yra.domain.playback.SleepTimerState
import com.example.yra.ui.components.NeuCard
import com.example.yra.ui.components.NeuButton

@Composable
fun OptionsScreen(viewModel: OptionsViewModel) {
    val eqEnabled by viewModel.equalizerManager.isEqualizerEnabled.collectAsState()
    val bands by viewModel.equalizerManager.bands.collectAsState()
    val timerState by viewModel.sleepTimerManager.timerState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
            )
        }

        // Sleep Timer Section
        item {
            NeuCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sleep Timer",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = when (val state = timerState) {
                            is SleepTimerState.Idle -> "No timer set"
                            is SleepTimerState.TimeBased -> "Time remaining: ${state.remainingSeconds / 60}m ${state.remainingSeconds % 60}s"
                            is SleepTimerState.TrackBased -> "Tracks remaining: ${state.remainingTracks}"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NeuButton(
                            onClick = { viewModel.setTimer(15, null) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("15m")
                        }
                        NeuButton(
                            onClick = { viewModel.setTimer(30, null) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("30m")
                        }
                        NeuButton(
                            onClick = { viewModel.setTimer(null, 5) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("5 Songs")
                        }
                    }
                    
                    if (timerState !is SleepTimerState.Idle) {
                        Spacer(modifier = Modifier.height(16.dp))
                        NeuButton(
                            onClick = { viewModel.setTimer(null, null) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancel Timer")
                        }
                    }
                }
            }
        }

        // Equalizer Section
        item {
            NeuCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Equalizer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = eqEnabled,
                            onCheckedChange = { viewModel.equalizerManager.setEqualizerEnabled(it) }
                        )
                    }

                    if (eqEnabled && bands.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
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
                    } else if (eqEnabled && bands.isEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Equalizer not available on this device or no active audio session.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
