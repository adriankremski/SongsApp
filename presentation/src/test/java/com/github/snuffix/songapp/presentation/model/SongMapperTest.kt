package com.github.snuffix.songapp.presentation.model

import com.github.snuffix.songapp.domain.model.Song
import com.github.snuffix.songapp.presentation.mapper.SongViewMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class SongMapperTest {

    private val mapper = SongViewMapper()

    @Test
    fun mapFromEntityMapsData() {
        val song = SongDataFactory.makeSong(true)
        val songView = mapper.mapToView(song)
        assertEqualData(songView, song)
    }

    private fun assertEqualData(songView: SongView, song: Song) {
        assertEquals(songView.id, song.id)
        assertEquals(songView.trackName, song.trackName)
        assertEquals(songView.artistName, song.artistName)
        assertEquals(songView.releaseDate, song.releaseDate)
        assertEquals(songView.releaseYear, song.releaseYear)
        assertEquals(songView.imageUrl, song.imageUrl)
    }
}
