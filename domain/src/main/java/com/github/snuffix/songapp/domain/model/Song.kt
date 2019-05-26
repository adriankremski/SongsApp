package com.github.snuffix.songapp.domain.model

import java.util.*

data class Song(
    val id: String,
    val trackName: String,
    val releaseDate: Date? = null,
    val releaseYear: Int? = null,
    val imageUrl: String? = null,
    val isFromRemote: Boolean,
    val artistName: String
)