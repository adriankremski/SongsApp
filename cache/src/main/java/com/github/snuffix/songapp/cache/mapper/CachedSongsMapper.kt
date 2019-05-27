package com.github.snuffix.songapp.cache.mapper

import com.github.snuffix.songapp.cache.model.SongCachedModel
import com.github.snuffix.songapp.data.model.SongEntity


class CachedSongsMapper : ModelMapper<SongCachedModel, SongEntity> {
    override fun mapFromModel(model: SongCachedModel): SongEntity {
        return SongEntity(
            id = model.id,
            trackName = model.trackName,
            artistName = model.artistName,
            releaseYear = model.releaseYear
        )
    }
}