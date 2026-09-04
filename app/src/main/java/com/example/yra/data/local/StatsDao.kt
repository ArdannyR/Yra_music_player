package com.example.yra.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {
    @Insert
    suspend fun insertListeningHistory(history: ListeningHistoryEntity)

    @Query("SELECT SUM(durationMs) FROM listening_history WHERE timestamp >= :startTime AND timestamp <= :endTime")
    fun getTotalListeningTimeBetween(startTime: Long, endTime: Long): Flow<Long?>
    
    @Query("SELECT SUM(durationMs) FROM listening_history")
    fun getTotalHistoricalListeningTime(): Flow<Long?>
    
    @Query("SELECT SUM(durationMs) FROM listening_history WHERE timestamp >= :startTime")
    fun getTotalListeningTimeSince(startTime: Long): Flow<Long?>

    @Query("SELECT * FROM songs ORDER BY playCount DESC LIMIT :limit")
    fun getMostPlayedSongs(limit: Int): Flow<List<SongEntity>>
    
    @Query("UPDATE songs SET playCount = playCount + 1, totalTimeListened = totalTimeListened + :durationMs WHERE id = :songId")
    suspend fun updateSongStats(songId: Long, durationMs: Long)
    
    @Query("UPDATE songs SET totalTimeListened = totalTimeListened + :durationMs WHERE id = :songId")
    suspend fun addListeningTime(songId: Long, durationMs: Long)
}
