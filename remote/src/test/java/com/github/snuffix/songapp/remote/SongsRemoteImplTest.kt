package com.github.snuffix.songapp.remote

import com.github.snuffix.songapp.data.repository.RemoteException
import com.github.snuffix.songapp.remote.mapper.SongsMapper
import com.github.snuffix.songapp.remote.model.SongDataFactory
import com.github.snuffix.songapp.remote.model.SongsResponse
import com.github.snuffix.songapp.remote.service.ITunesSongsService
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SongsRemoteImplTest : BaseRemoteTest() {

    private val songsMapper = SongsMapper()
    private val service = mock<ITunesSongsService>()
    private val remote = SongsRemoteSourceImpl(service, songsMapper)

    private val testQuery = "Test"

    @Test
    fun searchSongsCallsServerWithCorrectParameters() {
        runBlocking {
            stubSearchSongs(SongDataFactory.makeSongsResponse(listOf()))
            remote.getSongs(testQuery, 0, 100)
            verify(service).searchSongsAsync(testQuery, 0, 100)
        }
    }

    private fun stubSearchSongs(model: SongsResponse) {
        runBlocking {
            whenever(service.searchSongsAsync(any(), any(), any())).thenReturn(deferredSuccessResponseOf(model))
        }
    }

    @Test
    fun searchSongsReturnsData() {
        runBlocking {
            val songs = SongDataFactory.makeSongsList(10)

            stubSearchSongs(SongDataFactory.makeSongsResponse(songs))

            val songEntities = songs.map { songsMapper.mapFromModel(it) }
            val result = remote.getSongs(testQuery, 0, 100)
            assertEquals(songEntities, result)
        }
    }

    @Test
    fun searchSongsThrowsRemoteException() {
        runBlocking {
            stubSearchSongsError(403)

            try {
                remote.getSongs(testQuery, 0, 100)
            } catch (exception: RemoteException) {
                assertEquals(403, exception.code)
            }
        }
    }

    private fun stubSearchSongsError(errorCode: Int) {
        runBlocking {
            whenever(service.searchSongsAsync(any(), any(), any())).thenReturn(deferredApiError(403, responseBodyOf(SongDataFactory.makeSongsResponse(listOf()))))
        }
    }
}