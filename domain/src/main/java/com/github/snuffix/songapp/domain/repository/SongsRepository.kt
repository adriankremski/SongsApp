package com.github.snuffix.songapp.domain.repository

import com.github.snuffix.songapp.domain.model.Result
import com.github.snuffix.songapp.domain.model.Song

interface SongsRepository {
    suspend fun getRemoteSongs(query: String, offset: Int): Result<List<Song>>
    suspend fun getLocalSongs(query: String, offset: Int): Result<List<Song>>
}