package com.example.yra.data.local

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "playlist_song_cross_ref",
    primaryKeys = ["playlistId", "id"],
    indices = [Index(value = ["id"])], // index on song id for faster queries
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        ),
        androidx.room.ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ]
)
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val id: Long // songId
)
