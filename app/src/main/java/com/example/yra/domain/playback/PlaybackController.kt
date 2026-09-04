package com.example.yra.domain.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.yra.data.local.SongEntity
import com.example.yra.data.service.YraMediaService
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaybackController(private val context: Context) {

    private var mediaController: MediaController? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentSong = MutableStateFlow<SongEntity?>(null)
    val currentSong: StateFlow<SongEntity?> = _currentSong.asStateFlow()

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, YraMediaService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            setupPlayerListener()
        }, MoreExecutors.directExecutor())
    }

    private fun setupPlayerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
            
            // To properly track the current song, we rely on the UI calling playSong() and updating _currentSong.
            // A more robust way in production would be encoding the Song ID in MediaMetadata and querying the DB here,
            // but for simplicity we sync it from the UI call.
        })
    }

    fun playSong(song: SongEntity) {
        _currentSong.value = song
        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(song.uri))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setAlbumTitle(song.album)
                    .build()
            )
            .build()
            
        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
        mediaController?.play()
    }
    
    fun pause() {
        mediaController?.pause()
    }
    
    fun resume() {
        mediaController?.play()
    }
    
    fun togglePlayPause() {
        if (_isPlaying.value) pause() else resume()
    }
    
    fun stop() {
        mediaController?.stop()
    }
    
    // Future implementation for playlists:
    // fun addPlaylist(songs: List<SongEntity>) { ... }
}
