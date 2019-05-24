package com.github.snuffix.songapp.cache.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.snuffix.songapp.cache.model.SongCachedModel


@Database(entities = [SongCachedModel::class], version = 1)
abstract class SongsDatabase constructor() : RoomDatabase() {
    abstract fun cachedSongsDao(): CachedSongsDao

    companion object {
        private var INSTANCE: SongsDatabase? = null
        private val lock = Any()
        fun getInstance(context: Context): SongsDatabase {
            if (INSTANCE == null) {
                synchronized(lock) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext, SongsDatabase::class.java, "songs.db"
                        ).fallbackToDestructiveMigration().build()
                    }
                    return INSTANCE!!
                }
            }
            return INSTANCE!!
        }
    }
}