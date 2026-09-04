package com.example.yra.domain.playback

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SleepTimerState {
    object Idle : SleepTimerState()
    data class TimeBased(val remainingSeconds: Int) : SleepTimerState()
    data class TrackBased(val remainingTracks: Int) : SleepTimerState()
}

class SleepTimerManager(
    private val playbackController: PlaybackController,
    private val coroutineScope: CoroutineScope
) {
    private val _timerState = MutableStateFlow<SleepTimerState>(SleepTimerState.Idle)
    val timerState: StateFlow<SleepTimerState> = _timerState.asStateFlow()

    private var countdownJob: Job? = null
    private var trackObservationJob: Job? = null

    fun setTimeTimer(minutes: Int) {
        cancelCurrentTimer()
        var remainingSeconds = minutes * 60
        _timerState.value = SleepTimerState.TimeBased(remainingSeconds)

        countdownJob = coroutineScope.launch(Dispatchers.Default) {
            while (remainingSeconds > 0) {
                delay(1000)
                remainingSeconds--
                _timerState.value = SleepTimerState.TimeBased(remainingSeconds)
            }
            triggerSleep()
        }
    }

    fun setTrackTimer(trackCount: Int) {
        cancelCurrentTimer()
        _timerState.value = SleepTimerState.TrackBased(trackCount)
        var tracksLeft = trackCount
        
        var isFirstCollection = true

        trackObservationJob = coroutineScope.launch(Dispatchers.Default) {
            playbackController.currentSong.collect { song ->
                if (song != null) {
                    if (isFirstCollection) {
                        isFirstCollection = false
                        return@collect
                    }
                    
                    tracksLeft--
                    if (tracksLeft <= 0) {
                        _timerState.value = SleepTimerState.TrackBased(0)
                        triggerSleep()
                    } else {
                        _timerState.value = SleepTimerState.TrackBased(tracksLeft)
                    }
                }
            }
        }
    }

    fun cancelTimer() {
        cancelCurrentTimer()
    }

    private fun cancelCurrentTimer() {
        countdownJob?.cancel()
        countdownJob = null
        trackObservationJob?.cancel()
        trackObservationJob = null
        _timerState.value = SleepTimerState.Idle
    }

    private fun triggerSleep() {
        cancelCurrentTimer()
        playbackController.pause()
    }
}
