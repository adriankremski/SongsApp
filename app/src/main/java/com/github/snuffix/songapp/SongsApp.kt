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
import com.github.snuffix.songapp.mapper.SongsMapper
import com.github.snuffix.songapp.presentation.Launcher
import com.github.snuffix.songapp.presentation.LauncherFactory
import com.github.snuffix.songapp.presentation.SongsViewModel
import com.github.snuffix.songapp.presentation.mapper.SongViewMapper
import com.github.snuffix.songapp.remote.SongsRemoteSourceImpl
import com.github.snuffix.songapp.remote.mapper.RemoteSongsMapper
import com.github.snuffix.songapp.remote.service.ITunesSongServiceFactory
import com.github.snuffix.songapp.remote.service.NetworkCheck
import kotlinx.coroutines.CoroutineScope
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.experimental.builder.factoryBy
import org.koin.experimental.builder.singleBy
import timber.log.Timber
import timber.log.Timber.DebugTree

@Suppress("unused")
open class SongsApp : Application() {

    protected open val serverUrl: String = "https://itunes.apple.com/"
    protected open val testModules : List<Module> = listOf()

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

            val productionModules = listOf(
                cacheModule, remoteModule,
                dataModule, domainModule, presentationModule, uiModule
            )

            modules(productionModules + testModules)
        }
    }

    private val cacheModule = module {
        single { SongsDatabase.getInstance(get()) }
        single { CachedSongsMapper() }
        single { RawSongsMapper() }
        single { SongsParser() }
        factoryBy<SongsLocalSource, SongsLocalSourceImpl>()
    }

    private val remoteModule = module {
        single<NetworkCheck> {
            object : NetworkCheck {
                val connectivityManager = androidApplication().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                override fun isOnline(): Boolean {
                    val netInfo = connectivityManager.activeNetworkInfo
                    return (netInfo != null && netInfo.isConnected)
                }
            }
        }

        single { RemoteSongsMapper() }
        single { ITunesSongServiceFactory.makeService(serverUrl, androidApplication().cacheDir, BuildConfig.DEBUG, get()) }
        factoryBy<SongsRemoteSource, SongsRemoteSourceImpl>()
    }

    private val dataModule = module {
        single { SongsEntityMapper() }
        singleBy<SongsRepository, SongsRepositoryImpl>()
    }

    private val domainModule = module {
        factory { SearchAllSongs(get()) }
        factory { SearchLocalSongs(get()) }
        factory { SearchRemoteSongs(get()) }
    }
    private val presentationModule = module {
        single { SongViewMapper() }
        factory<LauncherFactory> {
            object : LauncherFactory {
                override fun createLauncher(scope: CoroutineScope) = Launcher.Default(scope)
            }
        }
        viewModel { SongsViewModel(get(), searchLocalSongs = get(), searchRemoteSongs = get(), searchAllSongs = get(), mapper = get()) }
    }

    private val uiModule = module {
        single { SongsMapper() }
    }
}

