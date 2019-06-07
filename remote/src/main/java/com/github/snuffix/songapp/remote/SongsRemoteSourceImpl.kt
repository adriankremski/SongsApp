package com.github.snuffix.songapp.remote

import com.github.snuffix.songapp.data.model.SongEntity
import com.github.snuffix.songapp.data.repository.RemoteException
import com.github.snuffix.songapp.data.repository.SongsRemoteSource
import com.github.snuffix.songapp.remote.mapper.RemoteSongsMapper
import com.github.snuffix.songapp.remote.service.ITunesSongsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

class SongsRemoteSourceImpl constructor(
    private val service: ITunesSongsService,
    private val mapper: RemoteSongsMapper
) : SongsRemoteSource {

    override suspend fun getSongs(query: String, offset: Int, limit: Int): List<SongEntity> =
        withContext(Dispatchers.IO) {
            val response = service.searchSongsAsync(query = query, offset = offset, limit = limit).getBodyOrThrow()
            response.results.map { item -> mapper.mapFromModel(item) }
        }
}

fun <T : Any> Response<T>.getBodyOrThrow() = if (isSuccessful) {
    body() ?: throw IOException("Body can't be null")
} else {
    throw RemoteException(code())
}
