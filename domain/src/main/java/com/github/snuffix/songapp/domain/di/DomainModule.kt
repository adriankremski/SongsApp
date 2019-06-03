package com.github.snuffix.songapp.domain.di

import com.github.snuffix.songapp.domain.usecase.BaseRetryLogic
import com.github.snuffix.songapp.domain.usecase.SearchAllSongs
import com.github.snuffix.songapp.domain.usecase.SearchLocalSongs
import com.github.snuffix.songapp.domain.usecase.SearchRemoteSongs
import org.koin.dsl.module

val domainModule = module {
    factory { BaseRetryLogic() }
    factory { SearchAllSongs(get(), get()) }
    factory { SearchLocalSongs(get(), get()) }
    factory { SearchRemoteSongs(get(), get()) }
}
