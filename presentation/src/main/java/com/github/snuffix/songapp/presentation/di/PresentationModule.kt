package com.github.snuffix.songapp.presentation.di

import com.github.snuffix.songapp.presentation.Launcher
import com.github.snuffix.songapp.presentation.SongsViewModel
import com.github.snuffix.songapp.presentation.mapper.SongViewMapper
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    single { SongViewMapper() }
    factory<Launcher> {
        Launcher.Default()
    }
    viewModel {
        SongsViewModel(
            uiScopeLauncher = get(),
            searchLocalSongs = get(),
            searchRemoteSongs = get(),
            searchAllSongs = get(),
            mapper = get()
        )
    }
}
