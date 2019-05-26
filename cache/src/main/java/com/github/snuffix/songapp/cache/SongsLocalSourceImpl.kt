package com.github.snuffix.songapp.cache

import android.content.Context
import com.github.snuffix.songapp.cache.db.SongsDatabase
import com.github.snuffix.songapp.cache.mapper.CachedSongsMapper
import com.github.snuffix.songapp.cache.mapper.RawSongsMapper
import com.github.snuffix.songapp.cache.parser.SongsParser
import com.github.snuffix.songapp.data.model.SongEntity
import com.github.snuffix.songapp.data.repository.SongsLocalSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val BUFFER_SIZE = 200

open class SongsLocalSourceImpl constructor(
    private val context: Context,
    private val cachedSongsMapper: CachedSongsMapper,
    private val rawSongsMapper: RawSongsMapper,
    private val songsParser: SongsParser,
    private val songsDatabase: SongsDatabase
) : SongsLocalSource {

    override suspend fun getSongs(query: String, offset: Int, limit: Int): List<SongEntity> =
        withContext(Dispatchers.IO) {
            val songsInputStream = context.resources.openRawResource(R.raw.songs_list)

            val dao = songsDatabase.cachedSongsDao()

            if (dao.countSongs() == 0) {
                songsParser.readData(songsInputStream, bufferSize = BUFFER_SIZE) { rawSongs ->
                    val cachedSongs = rawSongs.map { rawSongsMapper.mapFromRawModel(it) }
                    dao.insertSongs(cachedSongs)
                }
            }

            dao.querySongs(query = "%$query%", limit = limit, offset = offset).map { cachedSongsMapper.mapFromModel(it) }
        }
}

