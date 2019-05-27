package com.github.snuffix.songapp.model

import com.github.snuffix.songapp.remote.model.SongModel
import org.joda.time.DateTime
import java.util.*


object SongDataFactory {
    private fun randomString(): String {
        return UUID.randomUUID().toString()
    }

    private fun randomInt(): Int {
        return Random().nextInt()
    }

    private fun makeSongModel(): SongModel {
        return SongModel(
            trackId = randomInt(),
            trackName = randomString(),
            releaseDate = DateTime.now().toString(),
            artworkUrl100 = randomString(),
            artistName = randomString()
        )
    }

    fun makeSongsList(count: Int) = List(count) { makeSongModel() }
}