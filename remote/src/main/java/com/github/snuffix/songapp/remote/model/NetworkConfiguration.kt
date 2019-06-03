package com.github.snuffix.songapp.remote.model

import java.io.File


interface NetworkConfiguration {
    val baseUrl: String
    val cacheDir: File
    val isDebug: Boolean
}