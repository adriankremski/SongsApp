package com.github.snuffix.songapp.cache.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.snuffix.songapp.cache.model.SongCachedModel

@Dao
interface CachedSongsDao {
    @Query(SongDatabaseConstants.QUERY_COUNT)
    suspend fun countSongs(): Int

    @Query(SongDatabaseConstants.QUERY_SONGS)
    suspend fun querySongs(query: String, limit: Int, offset: Int): List<SongCachedModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongCachedModel>)
}