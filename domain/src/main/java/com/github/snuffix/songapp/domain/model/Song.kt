package com.github.snuffix.songapp.domain.model

import java.util.*

class Song(
    val id: String,
    val trackName: String,
    val releaseDate: Date? = null,
    val releaseYear: Int? = null,
    val imageUrl: String? = null,
    val artistName: String
)