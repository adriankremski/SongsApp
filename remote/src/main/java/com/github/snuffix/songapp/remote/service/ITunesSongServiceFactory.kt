package com.github.snuffix.songapp.remote.service

import com.github.snuffix.songapp.data.repository.NoConnectivityException
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


object ITunesSongServiceFactory {
    fun makeService(cacheDir: File, isDebug: Boolean, networkCheck: NetworkCheck): ITunesSongsService {
        val okHttpClient = makeOkHttpClient(cacheDir, networkCheck, makeLoggingInterceptor((isDebug)))
        return makeService(okHttpClient)
    }

    private fun makeService(okHttpClient: OkHttpClient): ITunesSongsService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
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
//            builder.cacheControl(CacheControl.FORCE_CACHE)
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
