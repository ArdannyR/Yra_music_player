package com.example.yra.ui.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yra.domain.playback.EqualizerManager
import com.example.yra.domain.playback.SleepTimerManager

class OptionsViewModel(
    val equalizerManager: EqualizerManager,
    val sleepTimerManager: SleepTimerManager
) : ViewModel() {

    fun setTimer(minutes: Int?, tracks: Int?) {
        if (minutes != null && minutes > 0) {
            sleepTimerManager.setTimeTimer(minutes)
        } else if (tracks != null && tracks > 0) {
            sleepTimerManager.setTrackTimer(tracks)
        } else {
            sleepTimerManager.cancelTimer()
        }
    }
}

class OptionsViewModelFactory(
    private val equalizerManager: EqualizerManager,
    private val sleepTimerManager: SleepTimerManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OptionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OptionsViewModel(equalizerManager, sleepTimerManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
