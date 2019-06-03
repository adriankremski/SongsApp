package com.github.snuffix.songapp.domain.usecase

import com.github.snuffix.songapp.domain.model.CombinedResult
import com.github.snuffix.songapp.domain.model.Result
import com.github.snuffix.songapp.domain.model.Song
import com.github.snuffix.songapp.domain.repository.SongsRepository

open class SearchAllSongs constructor(retryLogic: BaseRetryLogic, private val songsRepository: SongsRepository) :
    BaseUseCase<List<Song>, SearchAllSongs.Params>(retryLogic) {

    override suspend fun buildUseCase(params: Params?): Result<List<Song>> {
        if (params == null) throw IllegalArgumentException("Params can't be null!")

        val remoteSongs = songsRepository.getRemoteSongs(params.query, params.remoteSongsOffset)
        val localSongs = songsRepository.getLocalSongs(params.query, params.localSongsOffset)

        val combinedResult = CombinedResult(remoteSongs, localSongs)

        return if (combinedResult.isOk()) {
            remoteSongs as Result.Ok
            localSongs as Result.Ok

            Result.Ok((remoteSongs.value + localSongs.value))
        } else {
            combinedResult.firstErrorResult()
        }
    }

    data class Params constructor(val query: String, val remoteSongsOffset: Int, val localSongsOffset: Int) {
        companion object {
            fun create(query: String, remoteSongsOffset: Int, localSongsOffset: Int): Params {
                return Params(query, remoteSongsOffset, localSongsOffset)
            }
        }
    }
}