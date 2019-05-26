package com.github.snuffix.songapp.data.repository


import com.github.snuffix.songapp.data.QUERY_LIMIT
import com.github.snuffix.songapp.data.SongsRepositoryImpl
import com.github.snuffix.songapp.data.mapper.SongsEntityMapper
import com.github.snuffix.songapp.data.model.SongDataFactory
import com.github.snuffix.songapp.data.model.SongEntity
import com.github.snuffix.songapp.domain.model.Result
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SongsRepositoryTest {

    private val mapper = SongsEntityMapper()
    private val remote = mock<SongsRemoteSource>()
    private val local = mock<SongsLocalSource>()
    private val repository = SongsRepositoryImpl(mapper, remote, local)

    @Before
    fun setup() {
    }

    @Test
    fun getRemoteSongsReturnsCorrectError() {
        runBlocking {
            assertGetRemoteSongsThrowsException<Result.CancelledError>(CancellationException())

            assertGetRemoteSongsThrowsException<Result.NetworkError>(NoConnectivityException())

            assertGetRemoteSongsThrowsException<Result.NetworkError>(UnknownHostException())
            assertGetRemoteSongsThrowsException<Result.NetworkError>(ConnectException())
            assertGetRemoteSongsThrowsException<Result.NetworkError>(SocketTimeoutException())
            assertGetRemoteSongsThrowsException<Result.ApiError>(RemoteException(403))
            assertGetRemoteSongsThrowsException<Result.Error>(Exception())
        }
    }

    private inline fun <reified CallResult : Result<*>> assertGetRemoteSongsThrowsException(exception: Exception) {
        runBlocking {
            val query = "Test"
            val offset = 0

            stubGetRemoteSongsThrowsException(exception)
            val result = repository.getRemoteSongs(query, offset)
            assert(result is CallResult)
        }
    }

    private fun stubGetRemoteSongsThrowsException(exception: Exception) {
        runBlocking {
            whenever(remote.getSongs(any(), any(), any())).thenAnswer { throw exception }
        }
    }

    @Test
    fun getRemoteSongsReturnsCorrectData() {
        runBlocking {
            val query = "Test"
            val offset = 0
            val songs = SongDataFactory.makeSongsList(10)

            stubGetRemoteSongsResult(songs)
            val result = repository.getRemoteSongs(query, offset)

            assertTrue(result is Result.Ok)
            result as Result.Ok
            assertEquals(songs.map { mapper.mapFromEntity(it, true) }, result.value)

            verify(remote).getSongs(query, offset = offset, limit = QUERY_LIMIT)
            verifyNoMoreInteractions(remote)
            verifyZeroInteractions(local)
        }
    }

    @Test
    fun getLocalSongsReturnsCorrectData() {
        runBlocking {
            val query = "Test"
            val offset = 0
            val songs = SongDataFactory.makeSongsList(10)

            stubGetLocalSongsResult(songs)
            val result = repository.getLocalSongs(query, offset)

            assertTrue(result is Result.Ok)
            result as Result.Ok
            assertEquals(songs.map { mapper.mapFromEntity(it, false) }, result.value)

            verify(local).getSongs(query, offset = offset, limit = QUERY_LIMIT)
            verifyNoMoreInteractions(local)
            verifyZeroInteractions(remote)
        }
    }

    private fun stubGetRemoteSongsResult(songs: List<SongEntity>) {
        runBlocking {
            whenever(remote.getSongs(any(), any(), any())).thenReturn(songs)
        }
    }

    private fun stubGetLocalSongsResult(songs: List<SongEntity>) {
        runBlocking {
            whenever(local.getSongs(any(), any(), any())).thenReturn(songs)
        }
    }
}