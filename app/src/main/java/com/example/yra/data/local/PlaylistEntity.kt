package com.example.yra.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PlaylistType {
    MANUAL,
    DIRECTORY
}

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Long = 0,
    val name: String,
    val type: PlaylistType = PlaylistType.MANUAL,
    val directoryPath: String? = null
)
