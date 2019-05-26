package com.github.snuffix.songapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.github.snuffix.songapp.cache.SongsLocalSourceImpl
import com.github.snuffix.songapp.cache.db.SongsDatabase
import com.github.snuffix.songapp.cache.mapper.CachedSongsMapper
import com.github.snuffix.songapp.cache.mapper.RawSongsMapper
import com.github.snuffix.songapp.cache.parser.SongsParser
import com.github.snuffix.songapp.data.SongsRepositoryImpl
import com.github.snuffix.songapp.data.mapper.SongsEntityMapper
import com.github.snuffix.songapp.data.repository.SongsLocalSource
import com.github.snuffix.songapp.data.repository.SongsRemoteSource
import com.github.snuffix.songapp.domain.repository.SongsRepository
import com.github.snuffix.songapp.domain.usecase.SearchAllSongs
import com.github.snuffix.songapp.domain.usecase.SearchLocalSongs
import com.github.snuffix.songapp.domain.usecase.SearchRemoteSongs
import com.github.snuffix.songapp.presentation.SongsViewModel
import com.github.snuffix.songapp.presentation.mapper.SongViewMapper
import com.github.snuffix.songapp.remote.SongsRemoteSourceImpl
import com.github.snuffix.songapp.remote.mapper.SongsMapper
import com.github.snuffix.songapp.remote.service.ITunesSongServiceFactory
import com.github.snuffix.songapp.remote.service.NetworkCheck
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import timber.log.Timber
import timber.log.Timber.DebugTree


@SuppressWarnings("unused")
class SongsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        JodaTimeAndroid.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        startKoin {
            // Android context
            androidContext(this@SongsApp)
            // modules
            modules(cacheModule, remoteModule, dataModule, domainModule, presentationModule, uiModule)
        }
    }
}

val cacheModule = module {
    single { SongsDatabase.getInstance(get()) }
    single { CachedSongsMapper() }
    single { RawSongsMapper() }
    single { SongsParser() }
    singleBy<SongsLocalSource, SongsLocalSourceImpl>()
}

val remoteModule = module {
    single<NetworkCheck> {
        object : NetworkCheck {
            val connectivityManager = androidApplication().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            override fun isOnline(): Boolean {
                val netInfo = connectivityManager.activeNetworkInfo
                return (netInfo != null && netInfo.isConnected)
            }
        }
    }

    single { SongsMapper() }
    single { ITunesSongServiceFactory.makeService(androidApplication().cacheDir, BuildConfig.DEBUG, get()) }
    singleBy<SongsRemoteSource, SongsRemoteSourceImpl>()
}

val dataModule = module {
    single { SongsEntityMapper() }
    singleBy<SongsRepository, SongsRepositoryImpl>()
}

val domainModule = module {
    single { SearchAllSongs(get()) }
    single { SearchLocalSongs(get()) }
    single { SearchRemoteSongs(get()) }
}

val presentationModule = module {
    single { SongViewMapper() }
    viewModel { SongsViewModel(searchLocalSongs = get(), searchRemoteSongs = get(), searchAllSongs = get(), mapper = get()) }
}

val uiModule = module {
    single { com.github.snuffix.songapp.mapper.SongsMapper() }
}