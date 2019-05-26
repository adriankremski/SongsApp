package com.github.snuffix.songapp.remote.service

import com.github.snuffix.songapp.data.repository.NoConnectivityException
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ITunesSongServiceFactory {
    fun makeService(isDebug: Boolean, networkCheck: NetworkCheck): ITunesSongsService {
        val okHttpClient = makeOkHttpClient(networkCheck, makeLoggingInterceptor((isDebug)))
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

    private fun makeOkHttpClient(networkCheck: NetworkCheck, httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(makeNetworkCheckInterceptor(networkCheck))
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    private fun makeNetworkCheckInterceptor(networkCheck: NetworkCheck): Interceptor = Interceptor { chain ->
        if (!networkCheck.isOnline()) {
            throw NoConnectivityException()
        }

        chain.proceed(chain.request())
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
