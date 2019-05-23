package com.github.snuffix.songapp.model

import com.github.snuffix.songapp.fragment.songs.adapter.ViewItem
import org.joda.time.DateTime

data class Song(
    val id: String,
    val artistName: String,
    val releaseDate: DateTime,
    val trackName: String
) : ViewItem
