package com.github.snuffix.songapp.remote.service

import com.github.snuffix.songapp.data.repository.NoConnectivityException
import com.github.snuffix.songapp.remote.model.NetworkConfiguration
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


object ITunesSongServiceFactory {
    fun makeService(networkConfiguration: NetworkConfiguration, networkCheck: NetworkCheck): ITunesSongsService {
        val okHttpClient = makeOkHttpClient(networkConfiguration.cacheDir, networkCheck, makeLoggingInterceptor((networkConfiguration.isDebug)))
        return makeService(networkConfiguration.baseUrl, okHttpClient)
    }

    private fun makeService(serverUrl: String, okHttpClient: OkHttpClient): ITunesSongsService {
        val retrofit = Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ITunesSongsService::class.java)
    }

    private fun makeOkHttpClient(cacheDir: File, networkCheck: NetworkCheck, httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val cacheSize = 10 * 1024 * 1024

        return OkHttpClient.Builder()
            .addInterceptor(makeNetworkCheckInterceptor(networkCheck))
            .cache(Cache(cacheDir, cacheSize.toLong()))
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    private fun makeNetworkCheckInterceptor(networkCheck: NetworkCheck): Interceptor = Interceptor { chain ->
        val builder = chain.request().newBuilder()

        if (!networkCheck.isOnline()) {
            throw NoConnectivityException()
        }

        chain.proceed(builder.build())
    }

    private fun makeLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (isDebug) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }
}
