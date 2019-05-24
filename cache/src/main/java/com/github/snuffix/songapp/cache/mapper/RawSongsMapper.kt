package com.github.snuffix.songapp.cache.mapper

import com.github.snuffix.songapp.cache.model.SongCachedModel
import com.github.snuffix.songapp.cache.model.SongRawModel
import java.util.*

open class RawSongsMapper : RawModelMapper<SongRawModel, SongCachedModel> {
    override fun mapFromRawModel(model: SongRawModel): SongCachedModel {
        return SongCachedModel(
            id = UUID.randomUUID().toString(),
            trackName = model.trackName,
            artistName = model.artistName,
            releaseYear = model.releaseYear.toIntOrNull()
        )
    }
}