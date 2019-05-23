package com.github.snuffix.songapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.snuffix.songapp.domain.model.Result
import com.github.snuffix.songapp.domain.model.Song
import com.github.snuffix.songapp.domain.usecase.SearchAllSongs
import com.github.snuffix.songapp.domain.usecase.SearchLocalSongs
import com.github.snuffix.songapp.domain.usecase.SearchRemoteSongs
import com.github.snuffix.songapp.presentation.extensions.map
import com.github.snuffix.songapp.presentation.mapper.SongViewMapper
import com.github.snuffix.songapp.presentation.model.Event
import com.github.snuffix.songapp.presentation.model.Resource
import com.github.snuffix.songapp.presentation.model.SongView
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

open class SongsViewModel constructor(
    private val searchLocalSongs: SearchLocalSongs,
    private val searchRemoteSongs: SearchRemoteSongs,
    private val searchAllSongs: SearchAllSongs,
    private val mapper: SongViewMapper
) : BaseViewModel() {

    private val songs = mutableListOf<SongView>()
    private val songsResource: MutableLiveData<Resource<List<SongView>>> = MutableLiveData()
    private val searchProgress: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val incrementalProgress: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private var hasMoreSongs = true
    private var currentJob: Job? = null
    private var lastQuery = ""

    val songsData = songsResource.map { this }
    val showIncrementalProgress = incrementalProgress.map { this }
    val showSearchProgress = searchProgress.map { this }
    var searchMode: SearchMode by Delegates.observable(SearchMode.ALL_SONGS) { _, oldMode, newMode ->
        if (oldMode != newMode) {
            searchSongs(lastQuery, true)
        }
    }

    fun searchSongs(query: String, forceFetch: Boolean = false) {
        if (query == lastQuery && !forceFetch) return

        lastQuery = query

        songs.clear()

        if (searchMode != SearchMode.LOCAL_SONGS) {
            searchProgress.postValue(Event(true))
        }

        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            val searchResult = when (searchMode) {
                SearchMode.ALL_SONGS -> searchAllSongs.execute(SearchAllSongs.Params.create(lastQuery, 0))
                SearchMode.ITUNES_SONGS -> searchRemoteSongs.execute(SearchRemoteSongs.Params.create(lastQuery, 0))
                SearchMode.LOCAL_SONGS -> searchLocalSongs.execute(SearchLocalSongs.Params.create(lastQuery, 0))
            }

            searchResult.whenOk {
                handleSearchSuccess(this)
            }.whenError {
                handleSearchError(this)
            }

            if (searchMode != SearchMode.LOCAL_SONGS) {
                searchProgress.postValue(Event(false))
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
            errorResult is Result.ApiError && errorResult.code == 403 -> {
                songsResource.postValue(Resource.apiError("Too many requests. Please wait"))
            }
            errorResult is Result.NetworkError -> {
                songsResource.postValue(Resource.networkError())
            }
            errorResult is Result.Error -> {
                songsResource.postValue(Resource.error(message = "Oops, something went wrong"))
            }
        }
    }

    fun searchSongsIncremental() {
        val searchInProgress = currentJob?.isCompleted != true

        if (!hasMoreSongs || searchInProgress) return

        incrementalProgress.postValue(Event(true))

        currentJob = viewModelScope.launch {
            val searchResult = when (searchMode) {
                SearchMode.ALL_SONGS -> {
                    //TODO ALL_SONGS -- OFFSET
                    val params = SearchAllSongs.Params.create(lastQuery, songs.size)
                    searchAllSongs.execute(params)
                }
                SearchMode.ITUNES_SONGS -> {
                    val params = SearchRemoteSongs.Params.create(lastQuery, songs.size)
                    searchRemoteSongs.execute(params)
                }
                SearchMode.LOCAL_SONGS -> {
                    val params = SearchLocalSongs.Params.create(lastQuery, songs.size)
                    searchLocalSongs.execute(params)
                }
            }

            searchResult.whenOk {
                handleSearchSuccess(this)
            }

            incrementalProgress.postValue(Event(false))
        }
    }

    fun List<Song>.mapToView() = map { mapper.mapToView(it) }
}


enum class SearchMode {
    ITUNES_SONGS, LOCAL_SONGS, ALL_SONGS
}