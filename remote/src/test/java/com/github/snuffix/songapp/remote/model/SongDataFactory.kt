package com.github.snuffix.songapp.remote.model

import org.joda.time.DateTime
import java.util.*

object SongDataFactory {
    private fun randomString(): String {
        return UUID.randomUUID().toString()
    }

    private fun randomInt(): Int {
        return Random().nextInt()
    }

    fun makeSongsResponse(songs: List<SongModel>)  = SongsResponse(songs)


    fun makeSongModel(): SongModel {
        return SongModel(
            trackId = randomInt(),
            trackName = randomString(),
            releaseDate = DateTime.now().toString(),
            artworkUrl100 = randomString(),
            artistName = randomString()
        )
    }

    fun makeSongsList(count: Int): List<SongModel> {
        val songs = mutableListOf<SongModel>()
        repeat(count) {
            songs.add(makeSongModel())
        }
        return songs
    }
}
