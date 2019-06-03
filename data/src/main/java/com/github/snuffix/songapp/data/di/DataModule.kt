package com.github.snuffix.songapp.data.di

import com.github.snuffix.songapp.data.SongsRepositoryImpl
import com.github.snuffix.songapp.data.mapper.SongsEntityMapper
import com.github.snuffix.songapp.domain.repository.SongsRepository
import org.koin.dsl.module

val dataModule = module {
    single { SongsEntityMapper() }
    single<SongsRepository> { SongsRepositoryImpl(get(), get(), get()) }
}
