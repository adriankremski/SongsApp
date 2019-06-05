package com.github.snuffix.songapp.domain.usecase

import com.github.snuffix.songapp.domain.model.CombinedResult
import com.github.snuffix.songapp.domain.model.Result
import com.github.snuffix.songapp.domain.model.Song
import com.github.snuffix.songapp.domain.repository.SongsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

open class SearchAllSongs constructor(retryLogic: BaseRetryLogic, private val songsRepository: SongsRepository) :
    BaseUseCase<Songs, SearchAllSongs.Params>(retryLogic) {

    override suspend fun execute(params: Params): Result<Songs> = withContext(Dispatchers.IO) {
        val remoteSongs = async { songsRepository.getRemoteSongs(params.query, params.remoteSongsOffset) }
        val localSongs = async { songsRepository.getLocalSongs(params.query, params.localSongsOffset) }

        val remoteSongsResult = remoteSongs.await()
        val localSongsResult = localSongs.await()

        val combinedResult = CombinedResult(remoteSongsResult, localSongsResult)

        if (combinedResult.isOk()) {
            remoteSongsResult as Result.Ok
            localSongsResult as Result.Ok

            Result.Ok(Songs(externalSongs = remoteSongsResult.value, localSongs = localSongsResult.value))
        } else {
            combinedResult.firstErrorResult()
        }
    }

    data class Params(val query: String, val remoteSongsOffset: Int = 0, val localSongsOffset: Int = 0)
}

data class Songs(val externalSongs: List<Song>, val localSongs: List<Song>)