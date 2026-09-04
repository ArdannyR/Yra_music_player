package com.example.yra.data.repository

import com.example.yra.data.local.SongDao
import com.example.yra.data.local.SongEntity
import com.example.yra.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow

class SongRepositoryImpl(
    private val songDao: SongDao,
    private val mediaStoreScanner: MediaStoreScanner
) : SongRepository {

    override fun getAllSongs(): Flow<List<SongEntity>> {
        return songDao.getAllSongs()
    }

    override suspend fun syncWithMediaStore(scanDirectories: Set<String>) {
        val mediaStoreSongs = mediaStoreScanner.scanAudioFiles(scanDirectories)
        
        if (mediaStoreSongs.isNotEmpty()) {
            // Insert new ones (ignores existing thanks to OnConflictStrategy.IGNORE)
            songDao.insertOrIgnoreSongs(mediaStoreSongs)
            
            // Delete songs from DB that are no longer in MediaStore
            val mediaStoreIds = mediaStoreSongs.map { it.id }
            songDao.deleteSongsNotIn(mediaStoreIds)
        }
    }

    override suspend fun updateSong(song: SongEntity) {
        songDao.updateSong(song)
    }

    override suspend fun toggleFavorite(songId: Long, isFavorite: Boolean) {
        val song = songDao.getSongById(songId)
        if (song != null) {
            songDao.updateSong(song.copy(isFavorite = isFavorite))
        }
    }

    override suspend fun deleteSong(id: Long) {
        songDao.deleteSong(id)
    }

    override suspend fun deleteSongsByPathPrefix(pathPrefix: String) {
        songDao.deleteSongsByPathPrefix(pathPrefix)
    }
}
