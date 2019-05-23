package com.github.snuffix.songapp.domain.usecase

import com.github.snuffix.songapp.domain.model.CombinedResult
import com.github.snuffix.songapp.domain.model.Result
import com.github.snuffix.songapp.domain.model.Song
import com.github.snuffix.songapp.domain.repository.SongsRepository

open class SearchAllSongs constructor(private val songsRepository: SongsRepository) :
    BaseUseCase<List<Song>, SearchAllSongs.Params>() {

    override suspend fun buildUseCase(params: Params?): Result<List<Song>> {
        if (params == null) throw IllegalArgumentException("Params can't be null!")

        val remoteSongs = songsRepository.getRemoteSongs(params.query, params.offset)
        val localSongs = songsRepository.getLocalSongs(params.query, params.offset)

        val combinedResult = CombinedResult(remoteSongs, localSongs)

        return if (combinedResult.isOk()) {
            remoteSongs as Result.Ok
            localSongs as Result.Ok

            Result.Ok((remoteSongs.value + localSongs.value))
        } else {
            combinedResult.firstErrorResult()
        }
    }

    data class Params constructor(val query: String, val offset: Int) {
        companion object {
            fun create(query: String, offset: Int): Params {
                return Params(query, offset)
            }
        }
    }
}