package com.github.snuffix.songapp.domain.model

import java.util.*

object SongDataFactory {
    private fun randomString(): String {
        return UUID.randomUUID().toString()
    }

    private fun randomBoolean(): Boolean {
        return Random().nextBoolean()
    }

    private fun randomInt(): Int {
        return Random().nextInt()
    }

    fun makeSong(): Song {
        return Song(
            id = randomString(),
            trackName = randomString(),
            releaseDate = Date(),
            releaseYear = randomInt(),
            imageUrl = randomString(),
            isFromRemote = randomBoolean(),
            artistName = randomString()
        )
    }

    fun makeSongsList(count: Int) = List(count) { makeSong() }
}
