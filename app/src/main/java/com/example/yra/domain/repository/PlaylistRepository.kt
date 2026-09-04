package com.example.yra.domain.repository

import com.example.yra.data.local.PlaylistEntity
import com.example.yra.data.local.PlaylistWithSongs
import com.example.yra.data.local.SongEntity
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
    
    suspend fun createPlaylist(name: String): Long
    
    suspend fun addSongToPlaylist(playlistId: Long, songId: Long)
    
    fun getPlaylistWithSongs(playlistId: Long): Flow<PlaylistWithSongs>
    
    fun getFavoriteSongs(): Flow<List<SongEntity>>
}
