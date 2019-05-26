package com.github.snuffix.songapp.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.snuffix.songapp.domain.model.Result
import com.github.snuffix.songapp.domain.model.Song
import com.github.snuffix.songapp.domain.usecase.SearchAllSongs
import com.github.snuffix.songapp.domain.usecase.SearchLocalSongs
import com.github.snuffix.songapp.domain.usecase.SearchRemoteSongs
import com.github.snuffix.songapp.presentation.mapper.SongViewMapper
import com.github.snuffix.songapp.presentation.model.Resource
import com.github.snuffix.songapp.presentation.model.SongDataFactory
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentCaptor

@RunWith(JUnit4::class)
class SongsViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private var searchLocalSongs = mock<SearchLocalSongs>()
    private var searchRemoteSongs = mock<SearchRemoteSongs>()
    private var searchAllSongs = mock<SearchAllSongs>()
    private var mapper = SongViewMapper()


    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    private val testQuery = "Test"

    private inline fun <reified T : Any> argumentCaptor(): ArgumentCaptor<T> = ArgumentCaptor.forClass(T::class.java)

    @Test
    fun searchExecutesLocalSongsUseCaseWithCorrectParams() {
        coroutinesTestRule.testDispatcher.runBlockingTest {
            stubSearchLocalSongs(SongDataFactory.makeSongsList(10, false))

            val songViewModel = SongsViewModel(SearchSource.LOCAL_SONGS, searchLocalSongs, searchRemoteSongs, searchAllSongs, mapper)
            songViewModel.searchSongs(testQuery, false)

            val paramsCaptor = argumentCaptor<SearchLocalSongs.Params>()
            verify(searchLocalSongs, times(1)).execute(paramsCaptor.capture())
            assertEquals(testQuery, paramsCaptor.value.query)
            assertEquals(0, paramsCaptor.value.offset)

            verifyZeroInteractions(searchAllSongs)
            verifyZeroInteractions(searchRemoteSongs)
        }
    }

    private fun stubSearchLocalSongs(songs: List<Song>) {
        runBlocking {
            whenever(searchLocalSongs.execute(any())).thenReturn(Result.Ok(songs))
        }
    }

    @Test
    fun searchLocalSongsReturnsCorrectData() {
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val songsList = SongDataFactory.makeSongsList(10, false)
            stubSearchLocalSongs(songsList)

            val songViewModel = SongsViewModel(SearchSource.LOCAL_SONGS, searchLocalSongs, searchRemoteSongs, searchAllSongs, mapper)
            songViewModel.searchSongs(testQuery, false)

            assertTrue(songViewModel.songsData().value is Resource.Success)
            val result = songViewModel.songsData().value as Resource.Success

            assertEquals(songsList.map { mapper.mapToView(it) }, result.data)
        }
    }

    @Test
    fun incrementalSearchExecutesAllLocalSongsUseCaseWithCorrectParams() {
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val localSongs = SongDataFactory.makeSongsList(5, false)
            val remoteSongs = SongDataFactory.makeSongsList(10, true)

            stubSearchAllSongs(localSongs + remoteSongs)

            val songViewModel = SongsViewModel(SearchSource.ALL_SONGS, searchLocalSongs, searchRemoteSongs, searchAllSongs, mapper)
            songViewModel.searchSongs(testQuery, false)
            songViewModel.searchSongsIncremental()

            val paramsCaptor = argumentCaptor<SearchAllSongs.Params>()
            verify(searchAllSongs, times(2)).execute(paramsCaptor.capture())
            assertEquals(testQuery, paramsCaptor.value.query)
            assertEquals(localSongs.size, paramsCaptor.value.localSongsOffset)
            assertEquals(remoteSongs.size, paramsCaptor.value.remoteSongsOffset)

            verifyZeroInteractions(searchLocalSongs)
            verifyZeroInteractions(searchRemoteSongs)
        }
    }

    private fun stubSearchAllSongs(songs: List<Song>) {
        runBlocking {
            whenever(searchAllSongs.execute(any())).thenReturn(Result.Ok(songs))
        }
    }
}