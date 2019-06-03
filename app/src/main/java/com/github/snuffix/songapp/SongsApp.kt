package com.github.snuffix.songapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.github.snuffix.songapp.cache.di.cacheModule
import com.github.snuffix.songapp.data.di.dataModule
import com.github.snuffix.songapp.domain.di.domainModule
import com.github.snuffix.songapp.mapper.SongsMapper
import com.github.snuffix.songapp.presentation.di.presentationModule
import com.github.snuffix.songapp.remote.di.remoteModule
import com.github.snuffix.songapp.remote.model.NetworkConfiguration
import com.github.snuffix.songapp.remote.service.NetworkCheck
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber
import timber.log.Timber.DebugTree

@Suppress("unused")
open class SongsApp : Application() {

    protected open val serverUrl: String = "https://itunes.apple.com/"
    protected open val testModules: List<Module> = listOf()

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
                cacheModule,
                remoteModule,
                dataModule,
                domainModule,
                presentationModule,
                applicationModule,
                uiModule
            )

            modules(productionModules + testModules)
        }
    }

    private val applicationModule = module {
        single<NetworkCheck> {
            object : NetworkCheck {
                val connectivityManager = androidApplication().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                override fun isOnline(): Boolean {
                    val netInfo = connectivityManager.activeNetworkInfo
                    return (netInfo != null && netInfo.isConnected)
                }
            }
        }
        single<NetworkConfiguration> {
            object : NetworkConfiguration {
                override val baseUrl = serverUrl
                override val cacheDir = androidApplication().cacheDir
                override val isDebug = BuildConfig.DEBUG
            }
        }
    }

    private val uiModule = module {
        single(named("TEST")) { SongsMapper() }
    }
}

