package com.example.yra.domain.playback

import com.example.yra.data.local.ListeningHistoryEntity
import com.example.yra.data.local.SongEntity
import com.example.yra.data.local.StatsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

class PlaybackStatsTracker(
    private val playbackController: PlaybackController,
    private val statsDao: StatsDao,
    private val coroutineScope: CoroutineScope
) {
    private var trackingJob: Job? = null
    private var sessionAccumulatedMs: Long = 0L
    private var currentTrackedSong: SongEntity? = null
    
    // We consider a playback to count towards "playCount" if they listen for at least 30 seconds
    private val MIN_MS_FOR_PLAY_COUNT: Long = 30_000L

    fun startTracking() {
        // Observe song changes
        coroutineScope.launch(Dispatchers.IO) {
            playbackController.currentSong.collectLatest { newSong ->
                // If song changes, flush previous session
                if (currentTrackedSong?.id != newSong?.id) {
                    flushSession()
                }
                currentTrackedSong = newSong
            }
        }
        
        // Observe playing state to start/stop the timer
        coroutineScope.launch(Dispatchers.IO) {
            playbackController.isPlaying.collectLatest { isPlaying ->
                if (isPlaying) {
                    startTimer()
                } else {
                    stopTimer()
                    flushSession()
                }
            }
        }
    }

    private fun startTimer() {
        trackingJob?.cancel()
        trackingJob = coroutineScope.launch(Dispatchers.IO) {
            while (true) {
                delay(1000L)
                sessionAccumulatedMs += 1000L
            }
        }
    }

    private fun stopTimer() {
        trackingJob?.cancel()
        trackingJob = null
    }

    private suspend fun flushSession() {
        val song = currentTrackedSong
        if (song != null && sessionAccumulatedMs > 0) {
            val durationToSave = sessionAccumulatedMs
            val timestamp = System.currentTimeMillis()
            
            // We use a small local copy in case sessionAccumulatedMs gets modified by timer
            sessionAccumulatedMs = 0L

            // 1. Insert history record
            val historyEntity = ListeningHistoryEntity(
                songId = song.id,
                timestamp = timestamp,
                durationMs = durationToSave
            )
            statsDao.insertListeningHistory(historyEntity)
            
            // 2. Update song overall stats
            if (durationToSave >= MIN_MS_FOR_PLAY_COUNT) {
                // Increment playCount and add time
                statsDao.updateSongStats(song.id, durationToSave)
            } else {
                // Just add time, playCount unchanged.
                statsDao.addListeningTime(song.id, durationToSave)
            }
        }
    }
}
