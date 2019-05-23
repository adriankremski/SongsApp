package com.github.snuffix.songapp.remote.mapper

import com.github.snuffix.songapp.data.model.SongEntity
import com.github.snuffix.songapp.remote.model.SongModel
import org.joda.time.DateTime


open class SongsMapper : ModelMapper<SongModel, SongEntity> {
    override fun mapFromModel(model: SongModel): SongEntity {
        return SongEntity(
            id = model.trackId.toString(),
            trackName = model.trackName,
            artistName = model.artistName,
            releaseDate = DateTime(model.releaseDate).toDate()
        )
    }
}