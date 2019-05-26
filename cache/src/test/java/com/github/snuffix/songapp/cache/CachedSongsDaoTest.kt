package com.github.snuffix.songapp.cache

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.github.snuffix.songapp.cache.db.SongsDatabase
import com.github.snuffix.songapp.cache.model.SongDataFactory
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class CachedSongsDaoTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val database = Room.inMemoryDatabaseBuilder(
        RuntimeEnvironment.application.applicationContext,
        SongsDatabase::class.java
    ).allowMainThreadQueries().build()

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun querySongsReturnsCorrectData() {
        runBlocking {
            val song = SongDataFactory.makeSongCachedModel("Test")
            database.cachedSongsDao().insertSongs(listOf(song))

            val songs = database.cachedSongsDao().querySongs("Test", 10, 0)

            assertEquals(listOf(song), songs)
        }
    }

    @Test
    fun incrementalQuerySongsReturnsCorrectData() {
        runBlocking {
            val songs = List(10) {
                SongDataFactory.makeSongCachedModel("Test")
            }

            database.cachedSongsDao().insertSongs(songs)

            val result = database.cachedSongsDao().querySongs("Test", 5, 5)

            assertEquals(songs.subList(5, 10), result)
        }
    }
}