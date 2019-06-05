package com.github.snuffix.songapp.domain.usecase

import com.github.snuffix.songapp.domain.model.Result
import com.github.snuffix.songapp.domain.model.Song
import com.github.snuffix.songapp.domain.repository.SongsRepository

open class SearchLocalSongs constructor(retryLogic: BaseRetryLogic, private val songsRepository: SongsRepository) :
    BaseUseCase<List<Song>, SearchLocalSongs.Params>(retryLogic) {

    override suspend fun execute(params: Params): Result<List<Song>> = songsRepository.getLocalSongs(params.query, params.offset)

    data class Params constructor(val query: String, val offset: Int)
}