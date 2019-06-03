package com.github.snuffix.songapp.domain.di

import com.github.snuffix.songapp.domain.usecase.SearchAllSongs
import com.github.snuffix.songapp.domain.usecase.SearchLocalSongs
import com.github.snuffix.songapp.domain.usecase.SearchRemoteSongs
import org.koin.dsl.module

val domainModule = module {
    factory { SearchAllSongs(get()) }
    factory { SearchLocalSongs(get()) }
    factory { SearchRemoteSongs(get()) }
}
