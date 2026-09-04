package com.example.yra

import android.app.Application
import com.example.yra.domain.playback.PlaybackController
import com.example.yra.domain.repository.UserPreferencesRepository

class YraApplication : Application() {

    lateinit var playbackController: PlaybackController
        private set

    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    override fun onCreate() {
        super.onCreate()
        playbackController = PlaybackController(this)
        userPreferencesRepository = UserPreferencesRepository(this)
    }
}
