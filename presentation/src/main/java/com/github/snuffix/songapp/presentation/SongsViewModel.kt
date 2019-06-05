package com.github.snuffix.songapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.snuffix.songapp.domain.model.Result
import com.github.snuffix.songapp.domain.model.Song
import com.github.snuffix.songapp.domain.usecase.*
import com.github.snuffix.songapp.presentation.mapper.SongViewMapper
import com.github.snuffix.songapp.presentation.model.Event
import com.github.snuffix.songapp.presentation.model.Resource
import com.github.snuffix.songapp.presentation.model.SongView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.koin.core.KoinComponent
import kotlin.properties.Delegates

const val INCREMENTAL_SEARCH_MAX_RETRIES = 2

class SongsViewModel constructor(
    startSearchSource: SearchSource = SearchSource.ALL_SONGS,
    uiScopeLauncher: Launcher,
    private val searchLocalSongs: SearchLocalSongs,
    private val searchRemoteSongs: SearchRemoteSongs,
    private val searchAllSongs: SearchAllSongs,
    private val mapper: SongViewMapper
) : BaseViewModel(uiScopeLauncher), KoinComponent {

    private var externalSongsOffset = 0
    private var localSongsOffset = 0
    private val displayedSongs = mutableListOf<SongView>()
    private val songsResource: MutableLiveData<Resource<List<SongView>>> = MutableLiveData()
    private val tooManyRequestsToast = MutableLiveData<Event<Boolean>>()

    private var hasMoreSongs = true
    private var currentJob: Job? = null
    private var lastQuery = ""
    var isIncrementalSearch = false

    fun songsData(): LiveData<Resource<List<SongView>>> = songsResource
    fun tooManyRequestsToast(): LiveData<Event<Boolean>> = tooManyRequestsToast

    val isTooManyRequestsError: (Result<Any>) -> Boolean = { it is Result.ApiError && it.code == 403 }

    var searchSource: SearchSource by Delegates.observable(startSearchSource) { _, oldMode, newMode ->
        if (oldMode != newMode) {
            searchSongs(lastQuery, true)
        }
    }

    fun searchSongs(query: String = lastQuery, forceFetch: Boolean = false) {
        if (query == lastQuery && !forceFetch) return

        lastQuery = query

        displayedSongs.clear()

        isIncrementalSearch = false

        if (searchSource != SearchSource.LOCAL_SONGS) {
            songsResource.postValue(Resource.Loading())
        }

        currentJob?.cancel()
        currentJob = launch {
            when (searchSource) {
                SearchSource.ALL_SONGS -> {
                    executeSearch(searchAllSongs, params = SearchAllSongs.Params(lastQuery)) {
                        externalSongsOffset = it.externalSongs.size
                        localSongsOffset = it.localSongs.size

                        displaySongs(it.externalSongs + it.localSongs)
                    }
                }
                SearchSource.REMOTE_SONGS -> {
                    executeSearch(searchRemoteSongs, params = SearchRemoteSongs.Params(lastQuery), displayData = ::displaySongs)
                }
                SearchSource.LOCAL_SONGS -> {
                    executeSearch(searchLocalSongs, params = SearchLocalSongs.Params(lastQuery), displayData = ::displaySongs)
                }
            }
        }
    }

    private suspend fun <DATA : Any, Params : Any> executeSearch(
        useCase: BaseUseCase<DATA, Params>,
        params: Params,
        displayData: (DATA) -> Unit
    ) {
        useCase.execute(params = params).whenOk {
            displayData(this.value)
        }.whenError {
            handleSearchError(this)
        }
    }

    private fun displaySongs(songs: List<Song>) {
        hasMoreSongs = songs.isNotEmpty()
        displayedSongs.addAll(songs.mapToView())
        songsResource.postValue(Resource.Success(displayedSongs.distinctBy { it.id }))
    }

    private fun handleSearchError(errorResult: Result.Error) {
        when {
            isTooManyRequestsError(errorResult) -> {
                songsResource.postValue(Resource.apiError("Too many requests. Please wait"))
            }
            errorResult is Result.NetworkError -> {
                songsResource.postValue(Resource.networkError())
            }
            else -> {
                songsResource.postValue(Resource.error())
            }
        }
    }

    fun searchSongsIncremental() {
        val searchInProgress = currentJob?.isCompleted != true

        if (!hasMoreSongs || searchInProgress) return

        if (searchSource != SearchSource.LOCAL_SONGS) {
            songsResource.postValue(Resource.Loading())
        }

        isIncrementalSearch = true

        currentJob = launch {
            when (searchSource) {
                SearchSource.ALL_SONGS -> {
                    val params = SearchAllSongs.Params(lastQuery, localSongsOffset = localSongsOffset, remoteSongsOffset = externalSongsOffset)

                    executeIncrementalSearch(params = params, useCase = searchAllSongs) {
                        externalSongsOffset += it.externalSongs.size
                        localSongsOffset += it.localSongs.size

                        displaySongs(it.externalSongs + it.localSongs)
                    }
                }
                SearchSource.REMOTE_SONGS -> {
                    val params = SearchRemoteSongs.Params(lastQuery, offset = displayedSongs.size)
                    executeIncrementalSearch(params = params, useCase = searchRemoteSongs) {
                        displaySongs(it)
                    }
                }
                SearchSource.LOCAL_SONGS -> {
                    val params = SearchLocalSongs.Params(lastQuery, offset = displayedSongs.size)
                    executeIncrementalSearch(params = params, useCase = searchLocalSongs) {
                        displaySongs(it)
                    }
                }
            }

        }
    }

    private suspend fun <DATA : Any, Params : Any> executeIncrementalSearch(
        useCase: BaseUseCase<DATA, Params>,
        params: Params,
        displayData: (DATA) -> Unit
    ) {
        useCase.executeWithRetry(params = params, emitFailedResult = true, maxRetries = INCREMENTAL_SEARCH_MAX_RETRIES) {
            isTooManyRequestsError(this)
        }.collect {
            val retryNumber = it.retryNumber
            val searchResult = it.result

            searchResult.whenOk {
                displayData(this.value)
            }.whenError {
                handleIncrementalSearchError(errorResult = this, retryNumber = retryNumber, maxRetries = INCREMENTAL_SEARCH_MAX_RETRIES)
            }
        }
    }

    private fun handleIncrementalSearchError(errorResult: Result.Error, retryNumber: Int, maxRetries: Int) {
        if (isTooManyRequestsError(errorResult) && retryNumber == 0) {
            tooManyRequestsToast.postValue(Event(true))
        } else if (retryNumber == maxRetries) {
            handleSearchError(errorResult)
        }
    }

    private fun List<Song>.mapToView() = map { mapper.mapToView(it) }
}

enum class SearchSource {
    REMOTE_SONGS, LOCAL_SONGS, ALL_SONGS
}