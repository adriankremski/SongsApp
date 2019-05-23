package com.github.snuffix.songapp.domain.usecase

import com.github.snuffix.songapp.domain.model.Result
import com.github.snuffix.songapp.domain.model.Song
import com.github.snuffix.songapp.domain.repository.SongsRepository

open class SearchRemoteSongs constructor(private val songsRepository: SongsRepository) :
    BaseUseCase<List<Song>, SearchRemoteSongs.Params>() {

    override suspend fun buildUseCase(params: Params?): Result<List<Song>> {
        if (params == null) throw IllegalArgumentException("Params can't be null!")
        return songsRepository.getRemoteSongs(params.query, params.offset)
    }

    data class Params constructor(val query: String, val offset: Int) {
        companion object {
            fun create(query: String, offset: Int): Params {
                return Params(query, offset)
            }
        }
    }
}