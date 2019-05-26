package com.github.snuffix.songapp.cache.model

import com.github.snuffix.songapp.cache.mapper.CachedSongsMapper
import com.github.snuffix.songapp.data.model.SongEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CachedSongMapperTest {
    private val mapper = CachedSongsMapper()

    @Test
    fun mapFromModelMapsData() {
        val model = SongDataFactory.makeSongCachedModel()
        val entity = mapper.mapFromModel(model)

        assertEqualData(model, entity)
    }

    private fun assertEqualData(model: SongCachedModel, entity: SongEntity) {
        assertEquals(model.id, entity.id)
        assertEquals(model.trackName, entity.trackName)
        assertEquals(model.releaseYear, entity.releaseYear)
        assertEquals(model.artistName, entity.artistName)
        assertEquals(null, entity.imageUrl)
        assertEquals(null, entity.releaseDate)
    }
}