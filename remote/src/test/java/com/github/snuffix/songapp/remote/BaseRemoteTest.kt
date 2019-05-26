package com.github.snuffix.songapp.remote

import com.google.gson.Gson
import kotlinx.coroutines.CompletableDeferred
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

abstract class BaseRemoteTest {

    private val gson = Gson()

    inline fun <reified T> deferredSuccessResponseOf(result: T) = CompletableDeferred<Response<T>>().apply {
        complete(Response.success(result))
    }

    fun responseBodyOf(instance: Any) = ResponseBody.create(MediaType.parse("application/json"), gson.toJson(instance))

    fun <T> deferredApiError(errorCode: Int, body: ResponseBody) = CompletableDeferred<Response<T>>().apply {
        complete(Response.error(errorCode, body))
    }
}