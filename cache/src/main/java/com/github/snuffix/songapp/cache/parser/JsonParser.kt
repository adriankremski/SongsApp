package com.github.snuffix.songapp.cache.parser

import java.io.InputStream

interface JsonParser<T> {
    suspend fun readData(source: InputStream, bufferSize: Int, onFlush: suspend (List<T>) -> Unit)
}