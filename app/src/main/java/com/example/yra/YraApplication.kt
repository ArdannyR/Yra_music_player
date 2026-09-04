package com.example.yra

import android.app.Application
import com.example.yra.domain.playback.PlaybackController
import com.example.yra.domain.playback.PlaybackStatsTracker
import com.example.yra.domain.playback.EqualizerManager
import com.example.yra.domain.playback.SleepTimerManager
import com.example.yra.domain.repository.UserPreferencesRepository
import com.example.yra.data.local.YraDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class YraApplication : Application() {

    lateinit var playbackController: PlaybackController
        private set
        
    lateinit var playbackStatsTracker: PlaybackStatsTracker
        private set
        
    lateinit var equalizerManager: EqualizerManager
        private set
        
    lateinit var sleepTimerManager: SleepTimerManager
        private set

    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set
        
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        playbackController = PlaybackController(this)
        userPreferencesRepository = UserPreferencesRepository(this)
        
        val database = YraDatabase.getDatabase(this)
        playbackStatsTracker = PlaybackStatsTracker(
            playbackController = playbackController,
            statsDao = database.statsDao(),
            coroutineScope = applicationScope
        )
        playbackStatsTracker.startTracking()
        
        equalizerManager = EqualizerManager()
        applicationScope.launch {
            playbackController.audioSessionId.collect { id ->
                id?.let { equalizerManager.initEqualizer(it) }
            }
        }
        
        sleepTimerManager = SleepTimerManager(
            playbackController = playbackController,
            coroutineScope = applicationScope
        )
    }
}
