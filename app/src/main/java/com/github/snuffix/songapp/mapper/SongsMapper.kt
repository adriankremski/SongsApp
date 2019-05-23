package com.github.snuffix.songapp.mapper

import com.github.snuffix.songapp.model.Song
import com.github.snuffix.songapp.presentation.model.SongView
import org.joda.time.DateTime


open class SongsMapper : ViewMapper<SongView, Song> {
    override fun mapToUIModel(song: SongView) = Song(
        id = song.id,
        trackName = song.trackName,
        artistName = song.artistName,
        releaseDate = DateTime(song.releaseDate)
    )
}