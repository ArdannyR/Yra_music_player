package com.example.yra.data.service

import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.yra.YraApplication

class YraMediaService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()
        
        // Configurar los atributos de audio (manejo automático de focus)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()
            
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true) // true = manage audio focus automatically
            .setHandleAudioBecomingNoisy(true) // Pausar cuando se desconectan los audífonos
            .build()
            
        mediaSession = MediaSession.Builder(this, player).build()
        
        val app = applicationContext as YraApplication
        app.playbackController.setAudioSessionId(player.audioSessionId)
    }

    // Retorna la sesión a los clientes (MediaController)
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    // Required since API 34 for FOREGROUND_SERVICE_MEDIA_PLAYBACK
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            // Stop service if we are paused or no media is loaded when swiped away
            stopSelf()
        }
    }
}
