package com.github.snuffix.songapp.cache.model

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class SongRawModel(
    @SerializedName("Song Clean") val trackName: String,
    @SerializedName("ARTIST CLEAN") val artistName: String,
    @SerializedName("Release Year") val releaseYear: String,
    @SerializedName("COMBINED") val COMBINED: String,
    @SerializedName("First?") val first: Int,
    @SerializedName("Year?") val year: String,
    @SerializedName("PlayCount") val playCount: Int,
    @SerializedName("F*G") val fG: Int
)
