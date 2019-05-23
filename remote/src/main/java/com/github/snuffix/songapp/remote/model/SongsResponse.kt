package com.github.snuffix.songapp.remote.model

import com.google.gson.annotations.SerializedName

class SongsResponse(@SerializedName("results") val results: List<SongModel>)