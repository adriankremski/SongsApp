package com.github.snuffix.songapp.data.model

import com.github.snuffix.songapp.data.mapper.SongsEntityMapper
import com.github.snuffix.songapp.domain.model.Song
import org.junit.Assert.assertEquals
import org.junit.Test

class SongMapperTest {

    private val mapper = SongsEntityMapper()

    @Test
    fun mapFromEntityMapsData() {
        val songEntity = SongDataFactory.makeSongEntity()
        val song = mapper.mapFromEntity(songEntity, true)
        assertEqualData(songEntity, song, true)
    }

    private fun assertEqualData(songEntity: SongEntity, song: Song, isFromRemote: Boolean) {
        assertEquals(songEntity.id, song.id)
        assertEquals(songEntity.trackName, song.trackName)
        assertEquals(songEntity.artistName, song.artistName)
        assertEquals(songEntity.releaseDate, song.releaseDate)
        assertEquals(songEntity.releaseYear, song.releaseYear)
        assertEquals(songEntity.imageUrl, song.imageUrl)
        assertEquals(isFromRemote, song.isFromRemote)
    }
}
