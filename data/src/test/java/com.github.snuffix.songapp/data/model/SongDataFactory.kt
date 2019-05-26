package com.github.snuffix.songapp.data.model

import java.util.*

object SongDataFactory {
    private fun randomString(): String {
        return UUID.randomUUID().toString()
    }

    private fun randomInt(): Int {
        return Random().nextInt()
    }

    fun makeSongEntity(): SongEntity {
        return SongEntity(
            id = randomString(),
            trackName = randomString(),
            releaseDate = Date(),
            releaseYear = randomInt(),
            imageUrl = randomString(),
            artistName = randomString()
        )
    }

    fun makeSongsList(count: Int): List<SongEntity> {
        val songs = mutableListOf<SongEntity>()
        repeat(count) {
            songs.add(makeSongEntity())
        }
        return songs
    }
}
