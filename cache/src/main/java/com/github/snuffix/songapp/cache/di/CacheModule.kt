package com.github.snuffix.songapp.cache.di

import com.github.snuffix.songapp.cache.SongsLocalSourceImpl
import com.github.snuffix.songapp.cache.db.SongsDatabase
import com.github.snuffix.songapp.cache.mapper.CachedSongsMapper
import com.github.snuffix.songapp.cache.mapper.RawSongsMapper
import com.github.snuffix.songapp.cache.parser.SongsParser
import com.github.snuffix.songapp.data.repository.SongsLocalSource
import org.koin.dsl.module
import org.koin.experimental.builder.factoryBy

val cacheModule = module {
    single { SongsDatabase.getInstance(get()) }
    single { CachedSongsMapper() }
    single { RawSongsMapper() }
    single { SongsParser() }
    factoryBy<SongsLocalSource, SongsLocalSourceImpl>()
}
