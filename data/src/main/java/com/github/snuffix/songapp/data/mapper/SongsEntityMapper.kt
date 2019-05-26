package com.github.snuffix.songapp.data.mapper

import com.github.snuffix.songapp.data.model.SongEntity
import com.github.snuffix.songapp.domain.model.Song

open class SongsEntityMapper : EntityMapper<SongEntity, Song> {
    override fun mapFromEntity(song: SongEntity, isFromRemote: Boolean): Song {
        return Song(
            id = song.id,
            trackName = song.trackName,
            releaseDate = song.releaseDate,
            releaseYear = song.releaseYear,
            artistName = song.artistName,
            imageUrl = song.imageUrl,
            isFromRemote = isFromRemote
        )
    }
}
