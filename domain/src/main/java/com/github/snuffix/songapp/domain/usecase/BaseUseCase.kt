package com.github.snuffix.songapp.domain.usecase

import com.github.snuffix.songapp.domain.model.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

abstract class BaseUseCase<DATA : Any, in Params> {
    abstract suspend fun buildUseCase(params: Params? = null): Result<DATA>

    open suspend fun execute(params: Params? = null) = buildUseCase(params)

    open suspend fun executeWithRetry(
        params: Params? = null,
        emitFailedResult: Boolean = false,
        retryCount: Int = 5,
        retryDelay: Long = TimeUnit.SECONDS.toMillis(1),
        shouldRetry: Result<DATA>.() -> Boolean
    ): Flow<RetryResult<DATA>> = flow {

        var retryNumber = 0

        while (true) {
            val result = execute(params)

            if (result.shouldRetry() && retryNumber != retryCount) {
                if (emitFailedResult) {
                    emit(RetryResult(retryNumber, result))
                }

                retryNumber++
                delay(retryDelay)
            } else {
                emit(RetryResult(retryNumber, result))

                break
            }
        }
    }
}

class RetryResult<T : Any>(val retryNumber: Int, val result: Result<T>)