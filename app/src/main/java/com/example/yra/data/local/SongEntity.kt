package com.example.yra.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: Long, // Usually MediaStore ID
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: String,
    val path: String,
    
    // Editables
    val genre: String? = null,
    val releaseDate: String? = null, // Or Long
    val lyrics: String? = null,
    
    // Custom campos
    val sourceUrl: String? = null,
    val secondarySourceUrl: String? = null,
    
    val isFavorite: Boolean = false,
    val dateAdded: Long,
    
    // Statistics
    val playCount: Int = 0,
    val totalTimeListened: Long = 0L
)
