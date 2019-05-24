package com.github.snuffix.songapp.data.model

import java.util.*

class SongEntity(
    val id: String,
    val trackName: String,
    val artistName: String,
    val releaseDate: Date? = null,
    val releaseYear: Int? = null,
    val imageUrl: String? = null
)