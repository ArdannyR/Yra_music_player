package com.example.yra.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "listening_history",
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("songId"),
        Index("timestamp")
    ]
)
data class ListeningHistoryEntity(
    @PrimaryKey(autoGenerate = true) val historyId: Long = 0,
    val songId: Long,
    val timestamp: Long, // End of the listening session
    val durationMs: Long // How long the user listened to the song
)
