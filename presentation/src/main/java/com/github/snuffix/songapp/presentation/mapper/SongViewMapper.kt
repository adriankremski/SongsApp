package com.github.snuffix.songapp.presentation.mapper

import com.github.snuffix.songapp.domain.model.Song
import com.github.snuffix.songapp.presentation.model.SongView

class SongViewMapper : Mapper<SongView, Song> {
    override fun mapToView(song: Song): SongView {
        return SongView(
            id = song.id,
            trackName = song.trackName,
            artistName = song.artistName,
            imageUrl = song.imageUrl,
            releaseDate = song.releaseDate,
            releaseYear = song.releaseYear,
            isFromRemote = song.isFromRemote
        )
    }
}