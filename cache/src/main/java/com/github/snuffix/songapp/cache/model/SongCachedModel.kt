package com.github.snuffix.songapp.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.snuffix.songapp.cache.db.SongDatabaseConstants

@Entity(tableName = SongDatabaseConstants.TABLE_NAME)
data class SongCachedModel(
    @PrimaryKey
    val id: String,
    val trackName: String,
    val artistName: String,
    val releaseYear: Int? = null
)
