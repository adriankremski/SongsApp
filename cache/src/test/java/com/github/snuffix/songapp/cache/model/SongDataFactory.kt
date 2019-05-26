package com.github.snuffix.songapp.cache.model

import com.google.gson.Gson
import java.util.*

object SongDataFactory {
    private val gson = Gson()

    private fun randomString(): String {
        return UUID.randomUUID().toString()
    }

    private fun randomInt(): Int {
        return Random().nextInt()
    }


    fun makeSongCachedModel(trackName: String? = null): SongCachedModel {
        return SongCachedModel(
            id = randomString(),
            trackName = trackName ?: randomString(),
            artistName = randomString()
        )
    }

    fun makeRawSongsJson(songs: List<SongRawModel>) = gson.toJson(songs)

    fun makeRawSongsList(count: Int) = List(count) { makeSongRawModel() }

    fun makeSongRawModel(): SongRawModel {
        return SongRawModel(
            trackName = randomString(),
            artistName = randomString(),
            releaseYear = randomString(),
            COMBINED = randomString(),
            first = randomInt(),
            year = randomString(),
            playCount = randomInt(),
            fG = randomInt()
        )
    }
}
