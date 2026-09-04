package com.example.yra.domain.repository

import com.example.yra.data.local.SongEntity
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getAllSongs(): Flow<List<SongEntity>>
    
    /**
     * Scans MediaStore, adds new songs to Room, and removes songs no longer in MediaStore.
     */
    suspend fun syncWithMediaStore(scanDirectories: Set<String> = emptySet())
    
    suspend fun updateSong(song: SongEntity)
    
    suspend fun toggleFavorite(songId: Long, isFavorite: Boolean)
    
    /**
     * Deletes the song from the database. Actual file deletion requires system intent/approval.
     */
    suspend fun deleteSong(id: Long)

    suspend fun deleteSongsByPathPrefix(pathPrefix: String)
}
