package com.github.snuffix.songapp.data.mapper

import com.github.snuffix.songapp.data.model.SongEntity
import com.github.snuffix.songapp.domain.model.Song

open class SongsEntityMapper : EntityMapper<SongEntity, Song> {
    override fun mapFromEntity(song: SongEntity): Song {
        return Song(
            id = song.id,
            trackName = song.trackName,
            artistName = song.artistName,
            releaseDate = song.releaseDate
        )
    }
}
