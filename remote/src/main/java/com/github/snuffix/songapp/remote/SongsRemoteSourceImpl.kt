package com.github.snuffix.songapp.remote

import com.github.snuffix.songapp.data.model.SongEntity
import com.github.snuffix.songapp.data.repository.RemoteException
import com.github.snuffix.songapp.data.repository.SongsRemoteSource
import com.github.snuffix.songapp.remote.mapper.SongsMapper
import com.github.snuffix.songapp.remote.service.ITunesSongsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

open class SongsRemoteSourceImpl constructor(
    private val service: ITunesSongsService,
    private val mapper: SongsMapper
) : SongsRemoteSource {

    override suspend fun getSongs(query: String, offset: Int, limit: Int): List<SongEntity> =
        withContext(Dispatchers.IO) {
            val response = service.searchSongs(query = query, offset = offset, limit = limit).await().getBodyOrThrow()
            response.results.map { item -> mapper.mapFromModel(item) }
        }
}

fun <T : Any> Response<T>.getBodyOrThrow() = if (isSuccessful) {
    body() ?: throw IOException("Body can't be null")
} else {
    throw RemoteException(code())
}
