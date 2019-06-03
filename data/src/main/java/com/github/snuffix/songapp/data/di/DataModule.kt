package com.github.snuffix.songapp.data.di

import com.github.snuffix.songapp.data.SongsRepositoryImpl
import com.github.snuffix.songapp.data.mapper.SongsEntityMapper
import com.github.snuffix.songapp.domain.repository.SongsRepository
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy

val dataModule = module {
    single { SongsEntityMapper() }
    singleBy<SongsRepository, SongsRepositoryImpl>()
}
