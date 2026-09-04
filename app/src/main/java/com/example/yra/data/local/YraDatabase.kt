package com.example.yra.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.room.TypeConverters

@Database(
    entities = [SongEntity::class, PlaylistEntity::class, PlaylistSongCrossRef::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class YraDatabase : RoomDatabase() {
    
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: YraDatabase? = null

        fun getDatabase(context: Context): YraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YraDatabase::class.java,
                    "yra_database"
                )
                .addMigrations(MIGRATION_2_3)
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                // Create the new table with foreign keys
                db.execSQL("""
                    CREATE TABLE new_playlist_song_cross_ref (
                        playlistId INTEGER NOT NULL,
                        id INTEGER NOT NULL,
                        PRIMARY KEY(playlistId, id),
                        FOREIGN KEY(playlistId) REFERENCES playlists(playlistId) ON DELETE CASCADE,
                        FOREIGN KEY(id) REFERENCES songs(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                // Copy the data
                db.execSQL("INSERT INTO new_playlist_song_cross_ref (playlistId, id) SELECT playlistId, id FROM playlist_song_cross_ref")
                // Remove the old table
                db.execSQL("DROP TABLE playlist_song_cross_ref")
                // Rename the new table
                db.execSQL("ALTER TABLE new_playlist_song_cross_ref RENAME TO playlist_song_cross_ref")
                // Recreate the index
                db.execSQL("CREATE INDEX index_playlist_song_cross_ref_id ON playlist_song_cross_ref(id)")
            }
        }
    }
}

