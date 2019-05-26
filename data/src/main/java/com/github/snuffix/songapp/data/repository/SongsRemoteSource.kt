package com.github.snuffix.songapp.data.repository

import com.github.snuffix.songapp.data.model.SongEntity
import java.io.IOException

interface SongsRemoteSource {
    suspend fun getSongs(query: String, offset: Int, limit: Int): List<SongEntity>
}

class RemoteException(val code: Int) : Exception()
class NoConnectivityException : IOException()