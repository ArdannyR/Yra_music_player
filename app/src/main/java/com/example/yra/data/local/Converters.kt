package com.example.yra.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromPlaylistType(value: PlaylistType): String {
        return value.name
    }

    @TypeConverter
    fun toPlaylistType(value: String): PlaylistType {
        return enumValueOf<PlaylistType>(value)
    }
}
