package com.github.snuffix.songapp.domain.usecase

import com.github.snuffix.songapp.domain.model.Result
import com.github.snuffix.songapp.domain.model.Song
import com.github.snuffix.songapp.domain.model.SongDataFactory
import com.github.snuffix.songapp.domain.repository.SongsRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import java.io.IOException

class SearchAllSongsTests {

    private lateinit var searchAllSongs: SearchAllSongs

    private val songsRepository: SongsRepository = mock()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        searchAllSongs = SearchAllSongs(songsRepository)
    }

    @Test
    fun searchAllSongsReturnsData() {
        runBlocking {
            val localSongs = SongDataFactory.makeSongsList(10)
            val remoteSongs = SongDataFactory.makeSongsList(10)

            stubGetLocalSongs(localSongs)
            stubGetRemoteSongs(remoteSongs)

            val query = "Test"
            val localSongsOffset = 15
            val remoteSongsOffset = 10
            val searchParams = SearchAllSongs.Params.create(query, remoteSongsOffset, localSongsOffset)
            val result = searchAllSongs.buildUseCase(searchParams)

            assertTrue(result is Result.Ok)
            result as Result.Ok
            assertEquals(result.value.sortedBy { it.id }, (localSongs + remoteSongs).sortedBy { it.id })

            verify(songsRepository).getLocalSongs(query, localSongsOffset)
            verify(songsRepository).getRemoteSongs(query, remoteSongsOffset)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun searchAllSongsThrowsError() {
        runBlocking {
            searchAllSongs.buildUseCase()
        }
    }

    @Test
    fun searchAllSongsReturnsErrorWhenRemoteFails() {
        runBlocking {
            stubGetRemoteSongsError()
            stubGetLocalSongs(SongDataFactory.makeSongsList(10))

            val result = searchAllSongs.buildUseCase(SearchAllSongs.Params.create("", 0, 0))

            assertTrue(result is Result.Error)
        }
    }

    @Test
    fun searchAllSongsReturnsErrorWhenLocalFails() {
        runBlocking {
            stubGetLocalSongsError()
            stubGetRemoteSongs(SongDataFactory.makeSongsList(10))

            val result = searchAllSongs.buildUseCase(SearchAllSongs.Params.create("", 0, 0))

            assertTrue(result is Result.Error)
        }
    }

    private fun stubGetLocalSongs(songs: List<Song>) {
        runBlocking {
            whenever(songsRepository.getLocalSongs(any(), any())).thenReturn(Result.Ok(songs))
        }
    }

    private fun stubGetRemoteSongs(songs: List<Song>) {
        runBlocking {
            whenever(songsRepository.getRemoteSongs(any(), any())).thenReturn(Result.Ok(songs))
        }
    }

    private fun stubGetLocalSongsError() {
        runBlocking {
            whenever(songsRepository.getLocalSongs(any(), any())).thenReturn(Result.Error(IOException()))
        }
    }

    private fun stubGetRemoteSongsError() {
        runBlocking {
            whenever(songsRepository.getRemoteSongs(any(), any())).thenReturn(Result.Error(IOException()))
        }
    }
}