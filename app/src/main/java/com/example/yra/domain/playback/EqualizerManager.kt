package com.example.yra.domain.playback

import android.media.audiofx.Equalizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EqualizerBand(
    val bandIndex: Short,
    val centerFreqHz: Int,
    val minLevel: Short,
    val maxLevel: Short,
    val currentLevel: Short
)

class EqualizerManager {

    private var equalizer: Equalizer? = null
    
    private val _isEqualizerEnabled = MutableStateFlow(false)
    val isEqualizerEnabled: StateFlow<Boolean> = _isEqualizerEnabled.asStateFlow()
    
    private val _bands = MutableStateFlow<List<EqualizerBand>>(emptyList())
    val bands: StateFlow<List<EqualizerBand>> = _bands.asStateFlow()

    fun initEqualizer(audioSessionId: Int) {
        if (audioSessionId == 0) return
        
        try {
            // Release previous if exists
            equalizer?.release()
            
            // Priority 0 is fine for Equalizer
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = _isEqualizerEnabled.value
            }
            
            updateBandsFlow()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateBandsFlow() {
        val eq = equalizer ?: return
        val numBands = eq.numberOfBands
        val range = eq.bandLevelRange // [min, max]
        
        val newBands = (0 until numBands).map { i ->
            val index = i.toShort()
            EqualizerBand(
                bandIndex = index,
                centerFreqHz = eq.getCenterFreq(index) / 1000, // convert milliHertz to Hz
                minLevel = range[0],
                maxLevel = range[1],
                currentLevel = eq.getBandLevel(index)
            )
        }
        _bands.value = newBands
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        _isEqualizerEnabled.value = enabled
        try {
            equalizer?.enabled = enabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBandLevel(bandIndex: Short, level: Short) {
        try {
            equalizer?.setBandLevel(bandIndex, level)
            // Update flow state
            _bands.value = _bands.value.map {
                if (it.bandIndex == bandIndex) it.copy(currentLevel = level) else it
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun release() {
        equalizer?.release()
        equalizer = null
    }
}
