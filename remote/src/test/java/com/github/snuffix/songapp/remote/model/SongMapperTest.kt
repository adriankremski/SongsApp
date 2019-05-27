package com.github.snuffix.songapp.remote.model

import com.github.snuffix.songapp.data.model.SongEntity
import com.github.snuffix.songapp.remote.mapper.RemoteSongsMapper
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class SongMapperTest {

    private val mapper = RemoteSongsMapper()

    @Test
    fun mapFromModelMapsData() {
        val songModel = SongDataFactory.makeSongModel()
        val songEntity = mapper.mapFromModel(songModel)
        assertEqualData(songModel, songEntity)
    }

    private fun assertEqualData(songModel: SongModel, songEntity: SongEntity) {
        assertEquals(songModel.trackId.toString(), songEntity.id)
        assertEquals(songModel.trackName, songEntity.trackName)
        assertEquals(songModel.artistName, songEntity.artistName)
        assertEquals(songModel.artworkUrl100, songEntity.imageUrl)
        assertEquals(null, songEntity.releaseYear)
        assertEquals(DateTime(songModel.releaseDate).toDate(), songEntity.releaseDate)
    }
}
