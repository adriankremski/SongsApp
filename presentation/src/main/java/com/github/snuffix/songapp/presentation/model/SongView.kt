package com.github.snuffix.songapp.presentation.model

import java.util.*

class SongView(
    val id: String,
    val trackName: String,
    val artistName: String,
    val imageUrl: String? = null,
    val releaseDate: Date? = null,
    val releaseYear: Int? = null
)
