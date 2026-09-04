package com.example.yra.domain.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.C
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

    private val _audioSessionId = MutableStateFlow<Int?>(null)
    val audioSessionId: StateFlow<Int?> = _audioSessionId.asStateFlow()
    
    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    // Store the current playlist to look up SongEntity by ID when track changes
    private var currentPlaylist: List<SongEntity> = emptyList()

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, YraMediaService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            setupPlayerListener()
            _isShuffleEnabled.value = mediaController?.shuffleModeEnabled ?: false
            _repeatMode.value = mediaController?.repeatMode ?: Player.REPEAT_MODE_OFF
        }, MoreExecutors.directExecutor())
    }

    private fun setupPlayerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
            
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                val songId = mediaItem?.mediaId?.toLongOrNull()
                _currentSong.value = currentPlaylist.find { it.id == songId } ?: _currentSong.value
            }
            
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _isShuffleEnabled.value = shuffleModeEnabled
            }
            
            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = repeatMode
            }
        })
    }
    
    fun setAudioSessionId(id: Int) {
        _audioSessionId.value = id
    }

    fun playSong(song: SongEntity) {
        playPlaylist(listOf(song), 0)
    }
    
    fun playPlaylist(songs: List<SongEntity>, startIndex: Int = 0) {
        currentPlaylist = songs
        _currentSong.value = songs.getOrNull(startIndex)
        
        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(Uri.parse(song.uri))
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setAlbumTitle(song.album)
                        .build()
                )
                .build()
        }

        mediaController?.setMediaItems(mediaItems, startIndex, C.TIME_UNSET)
        mediaController?.prepare()
        mediaController?.play()
    }
    
    fun toggleShuffle() {
        mediaController?.let {
            it.shuffleModeEnabled = !it.shuffleModeEnabled
        }
    }
    
    fun toggleRepeat() {
        mediaController?.let {
            val nextMode = when (it.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }
            it.repeatMode = nextMode
        }
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
    
    fun skipToNext() {
        mediaController?.seekToNextMediaItem()
    }
    
    fun skipToPrevious() {
        mediaController?.seekToPreviousMediaItem()
    }
}
