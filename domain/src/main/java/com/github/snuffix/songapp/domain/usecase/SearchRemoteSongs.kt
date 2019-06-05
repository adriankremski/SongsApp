package com.github.snuffix.songapp.domain.usecase

import com.github.snuffix.songapp.domain.model.Result
import com.github.snuffix.songapp.domain.model.Song
import com.github.snuffix.songapp.domain.repository.SongsRepository

open class SearchRemoteSongs constructor(retryLogic: BaseRetryLogic, private val songsRepository: SongsRepository) :
    BaseUseCase<List<Song>, SearchRemoteSongs.Params>(retryLogic) {

    override suspend fun execute(params: Params): Result<List<Song>> = songsRepository.getRemoteSongs(params.query, params.offset)

    data class Params(val query: String, val offset: Int)
}