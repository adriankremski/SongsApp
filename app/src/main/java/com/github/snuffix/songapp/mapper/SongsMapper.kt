package com.github.snuffix.songapp.mapper

import com.github.snuffix.songapp.model.Song
import com.github.snuffix.songapp.presentation.model.SongView
import org.joda.time.DateTime


class SongsMapper : ViewMapper<SongView, Song> {
    override fun mapToUIModel(song: SongView) = Song(
        id = song.id,
        trackName = song.trackName,
        artistName = song.artistName,
        imageUrl = song.imageUrl,
        releaseDate = if (song.releaseDate != null) DateTime(song.releaseDate) else null,
        releaseYear = song.releaseYear
    )
}