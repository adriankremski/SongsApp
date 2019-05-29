package com.github.snuffix.songapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.snuffix.songapp.domain.model.Result
import com.github.snuffix.songapp.domain.model.Song
import com.github.snuffix.songapp.domain.usecase.SearchAllSongs
import com.github.snuffix.songapp.domain.usecase.SearchLocalSongs
import com.github.snuffix.songapp.domain.usecase.SearchRemoteSongs
import com.github.snuffix.songapp.presentation.mapper.SongViewMapper
import com.github.snuffix.songapp.presentation.model.Resource
import com.github.snuffix.songapp.presentation.model.SongView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import org.koin.core.KoinComponent
import timber.log.Timber
import kotlin.properties.Delegates

class SongsViewModel constructor(
    launcherFactory: LauncherFactory = DefaultLauncherFactory(),
    startSearchSource: SearchSource = SearchSource.ALL_SONGS,
    private val searchLocalSongs: SearchLocalSongs,
    private val searchRemoteSongs: SearchRemoteSongs,
    private val searchAllSongs: SearchAllSongs,
    private val mapper: SongViewMapper
) : BaseViewModel(launcherFactory), KoinComponent {

    private val songs = mutableListOf<SongView>()
    private val songsResource: MutableLiveData<Resource<List<SongView>>> = MutableLiveData()

    private var hasMoreSongs = true
    private var currentJob: Job? = null
    private var lastQuery = ""
    var isIncrementalSearch = false

    fun songsData(): LiveData<Resource<List<SongView>>> = songsResource

    val isTooManyRequestsError: (Result<List<Song>>) -> Boolean = { it is Result.ApiError && it.code == 403 }

    var searchSource: SearchSource by Delegates.observable(startSearchSource) { _, oldMode, newMode ->
        if (oldMode != newMode) {
            searchSongs(lastQuery, true)
        }
    }

    fun searchSongs(query: String = lastQuery, forceFetch: Boolean = false) {
        if (query == lastQuery && !forceFetch) return

        lastQuery = query

        songs.clear()

        isIncrementalSearch = false

        if (searchSource != SearchSource.LOCAL_SONGS) {
            songsResource.postValue(Resource.Loading())
        }

        currentJob?.cancel()
        currentJob = uiScope.launch {

            val searchResultFlow = when (searchSource) {
                SearchSource.ALL_SONGS -> {
                    Timber.d("Fetching local songs with offset 0, and remote with offset 0")
                    searchAllSongs.executeWithRetry(SearchAllSongs.Params.create(lastQuery, 0, 0)) {
                        isTooManyRequestsError(this)
                    }
                }
                SearchSource.REMOTE_SONGS -> {
                    Timber.d("Fetching remote songs with offset 0")
                    searchRemoteSongs.executeWithRetry(SearchRemoteSongs.Params.create(lastQuery, 0)) {
                        isTooManyRequestsError(this)
                    }
                }
                SearchSource.LOCAL_SONGS -> {
                    Timber.d("Fetching local songs with offset 0")
                    searchLocalSongs.executeWithRetry(SearchLocalSongs.Params.create(lastQuery, 0)) {
                        isTooManyRequestsError(this)
                    }
                }
            }

            searchResultFlow.collect {
                val searchResult = it.result

                searchResult.whenOk {
                    handleSearchSuccess(this)
                }.whenError {
                    handleSearchError(this)
                }
            }

        }
    }

    private fun handleSearchSuccess(result: Result.Ok<List<Song>>) {
        hasMoreSongs = result.value.isNotEmpty()
        songs.addAll(result.value.mapToView())
        songsResource.postValue(Resource.Success(songs.distinctBy { it.id }))
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

        currentJob = uiScope.launch {
            val searchResult = when (searchSource) {
                SearchSource.ALL_SONGS -> {
                    val localSongsOffset = songs.count { !it.isFromRemote }
                    val remoteSongsOffset = songs.size - localSongsOffset

                    Timber.d("Fetching local songs with offset $localSongsOffset, and remote with offset $remoteSongsOffset")
                    val params = SearchAllSongs.Params.create(lastQuery, localSongsOffset = localSongsOffset, remoteSongsOffset = remoteSongsOffset)
                    searchAllSongs.execute(params)
                }
                SearchSource.REMOTE_SONGS -> {
                    val offset = songs.size
                    Timber.d("Fetching remote songs with offset $offset")
                    val params = SearchRemoteSongs.Params.create(lastQuery, offset)
                    searchRemoteSongs.execute(params)
                }
                SearchSource.LOCAL_SONGS -> {
                    val offset = songs.size
                    Timber.d("Fetching local songs with offset $offset")
                    val params = SearchLocalSongs.Params.create(lastQuery, offset)
                    searchLocalSongs.execute(params)
                }
            }

            searchResult.whenOk {
                handleSearchSuccess(this)
            }.whenError {
                handleSearchError(this)
            }
        }
    }

    private fun List<Song>.mapToView() = map { mapper.mapToView(it) }
}

enum class SearchSource {
    REMOTE_SONGS, LOCAL_SONGS, ALL_SONGS
}