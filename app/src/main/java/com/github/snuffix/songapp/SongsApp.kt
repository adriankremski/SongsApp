package com.github.snuffix.songapp

import android.app.Application
import com.github.snuffix.songapp.presentation.SongsViewModel
import com.github.snuffix.songapp.remote.SongsRemoteSourceImpl
import com.github.snuffix.songapp.data.SongRepositoryImpl
import com.github.snuffix.songapp.data.mapper.SongsEntityMapper
import com.github.snuffix.songapp.data.repository.SongsRemoteSource
import com.github.snuffix.songapp.domain.repository.SongsRepository
import com.github.snuffix.songapp.domain.usecase.SearchAllSongs
import com.github.snuffix.songapp.presentation.mapper.SongViewMapper
import com.github.snuffix.songapp.domain.usecase.SearchLocalSongs
import com.github.snuffix.songapp.domain.usecase.SearchRemoteSongs
import com.github.snuffix.songapp.remote.mapper.SongsMapper
import com.github.snuffix.songapp.remote.service.ITunesSongServiceFactory
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy

class SongsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        JodaTimeAndroid.init(this)

        startKoin {
            // Android context
            androidContext(this@SongsApp)
            // modules
            modules(remoteModule, dataModule, domainModule, presentationModule, uiModule)
        }
    }
}

val remoteModule = module {
    single { SongsMapper() }
    single { ITunesSongServiceFactory.makeService(BuildConfig.DEBUG) }
    singleBy<SongsRemoteSource, SongsRemoteSourceImpl>()
}

val dataModule = module {
    single { SongsEntityMapper() }
    singleBy<SongsRepository, SongRepositoryImpl>()
}

val domainModule = module {
    single { SearchAllSongs(get()) }
    single { SearchLocalSongs(get()) }
    single { SearchRemoteSongs(get()) }
}

val presentationModule = module {
    single { SongViewMapper() }
    viewModel { SongsViewModel(get(), get(), get(), get()) }
}

val uiModule = module {
    single { com.github.snuffix.songapp.mapper.SongsMapper() }
}