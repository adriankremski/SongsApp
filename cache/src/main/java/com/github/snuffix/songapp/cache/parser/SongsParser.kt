package com.github.snuffix.songapp.cache.parser

import com.github.snuffix.songapp.cache.model.SongRawModel
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.io.InputStream
import java.io.InputStreamReader


class SongsParser : JsonParser<SongRawModel> {
    override suspend fun readData(source: InputStream, bufferSize: Int, onFlush: suspend (List<SongRawModel>) -> Unit) {
        ParserUtil.readData(source, bufferSize, onFlush)
    }
}

object ParserUtil {
    suspend inline fun <reified T> readData(
        source: InputStream,
        bufferSize: Int,
        crossinline onFlush: suspend (List<T>) -> Unit
    ) {
        val items = mutableListOf<T>()

        try {
            JsonReader(InputStreamReader(source, "UTF-8")).use { reader ->
                val gson = GsonBuilder().create()

                reader.beginArray()

                val typeToken = object : TypeToken<T>() {}.type

                while (reader.hasNext()) {
                    items.add(gson.fromJson<T>(reader, typeToken))

                    if (items.size == bufferSize) {
                        onFlush(items)
                        items.clear()
                    }
                }

                onFlush(items)
                reader.endArray()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

