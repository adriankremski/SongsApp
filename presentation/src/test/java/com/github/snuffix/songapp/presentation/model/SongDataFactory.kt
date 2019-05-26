package com.github.snuffix.songapp.presentation.model

import com.github.snuffix.songapp.domain.model.Song
import java.util.*

object SongDataFactory {
    private fun randomString(): String {
        return UUID.randomUUID().toString()
    }

    private fun randomInt(): Int {
        return Random().nextInt()
    }

    fun makeSong(isFromRemote: Boolean): Song {
        return Song(
            id = randomString(),
            trackName = randomString(),
            releaseDate = Date(),
            releaseYear = randomInt(),
            imageUrl = randomString(),
            artistName = randomString(),
            isFromRemote = isFromRemote
        )
    }

    fun makeSongsList(count: Int, isFromRemote: Boolean) = List(count) { makeSong(isFromRemote) }
}
