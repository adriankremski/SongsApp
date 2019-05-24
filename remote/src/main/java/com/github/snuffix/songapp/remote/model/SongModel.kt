package com.github.snuffix.songapp.remote.model

import com.google.gson.annotations.SerializedName

class SongModel(
    @SerializedName("artworkUrl100") val artworkUrl100: String? = null,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("trackName") val trackName: String,
    @SerializedName("releaseDate") val releaseDate: String,
    @SerializedName("trackId") val trackId: Int
)
