package com.example.yra.data.repository

import com.example.yra.data.local.PlaylistDao
import com.example.yra.data.local.PlaylistEntity
import com.example.yra.data.local.PlaylistSongCrossRef
import com.example.yra.data.local.PlaylistType
import com.example.yra.data.local.PlaylistWithSongs
import com.example.yra.data.local.SongEntity
import com.example.yra.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<PlaylistEntity>> {
        return playlistDao.getAllPlaylists()
    }

    override suspend fun createPlaylist(name: String): Long {
        val playlist = PlaylistEntity(
            name = name,
            type = PlaylistType.MANUAL
        )
        return playlistDao.insertPlaylist(playlist)
    }

    override suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        val crossRef = PlaylistSongCrossRef(playlistId, songId)
        playlistDao.insertPlaylistSongCrossRef(crossRef)
    }

    override fun getPlaylistWithSongs(playlistId: Long): Flow<PlaylistWithSongs> {
        return playlistDao.getPlaylistWithSongs(playlistId)
    }

    override fun getFavoriteSongs(): Flow<List<SongEntity>> {
        return playlistDao.getFavoriteSongs()
    }
}
