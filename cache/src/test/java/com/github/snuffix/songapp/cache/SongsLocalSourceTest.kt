package com.github.snuffix.songapp.cache

import android.content.Context
import android.content.res.Resources
import com.github.snuffix.songapp.cache.db.CachedSongsDao
import com.github.snuffix.songapp.cache.db.SongsDatabase
import com.github.snuffix.songapp.cache.mapper.CachedSongsMapper
import com.github.snuffix.songapp.cache.mapper.RawSongsMapper
import com.github.snuffix.songapp.cache.model.SongDataFactory
import com.github.snuffix.songapp.cache.parser.SongsParser
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.ByteArrayInputStream

@RunWith(JUnit4::class)
class SongsLocalSourceTest {

    private val context = mock<Context>()
    private val resources = mock<Resources>()
    private val songsDao = mock<CachedSongsDao>()
    private val rawSongsMapper = RawSongsMapper()
    private val songsMapper = CachedSongsMapper()
    private val songsDatabase = mock<SongsDatabase>()

    private val songsLocalSource = SongsLocalSourceImpl(context, songsMapper, rawSongsMapper, SongsParser(), songsDatabase)
    private val rawSongs = SongDataFactory.makeRawSongsList(310)
    private val songs = rawSongs.map { rawSongsMapper.mapFromRawModel(it) }

    @Before
    fun setup() {
        whenever(context.resources).thenReturn(resources)
        val rawSongsJson = SongDataFactory.makeRawSongsJson(rawSongs)
        whenever(resources.openRawResource(any())).thenReturn(ByteArrayInputStream(rawSongsJson.toByteArray()))
        whenever(songsDatabase.cachedSongsDao()).thenReturn(songsDao)
    }


    @Test
    fun getSongsReadsRawDataFromFileIfDatabaseIsEmpty() {
        runBlocking {
            whenever(songsDao.countSongs()).thenReturn(0)
            whenever(songsDao.querySongs(any(), any(), any())).thenReturn(songs)

            songsLocalSource.getSongs("Test", offset = 0, limit = 100)

            verify(songsDao, times(2)).insertSongs(any())
            verify(songsDao).querySongs("%Test%", offset = 0, limit = 100)
        }
    }

    @Test
    fun getSongsSkipsRawFileIfDatabaseIsNotEmpty() {
        runBlocking {
            whenever(songsDao.countSongs()).thenReturn(1)
            whenever(songsDao.querySongs(any(), any(), any())).thenReturn(songs)

            songsLocalSource.getSongs("Test", offset = 0, limit = 100)

            verify(songsDao, times(0)).insertSongs(any())
            verify(songsDao).querySongs("%Test%", offset = 0, limit = 100)
        }
    }
}