package com.github.snuffix.songapp.remote.service

import com.github.snuffix.songapp.remote.model.SongsResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesSongsService {
    @GET("search?entity=song&media=music&attribute=songTerm")
    fun searchSongs(
        @Query("term") query: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Deferred<Response<SongsResponse>>
}
