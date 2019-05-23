package com.github.snuffix.songapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    private val songsResource: MutableLiveData<Resource<List<SongView>>> = MutableLiveData()
    val songsData = songsResource.map { this }
    private val songs = mutableListOf<SongView>()

    private val incrementalProgress: MutableLiveData<Event<Boolean>> = MutableLiveData()

    val showIncrementalProgress = incrementalProgress.map { this }

    private var hasMoreSongs = true
    private var currentJob: Job? = null

    var searchMode: SearchMode by Delegates.observable(SearchMode.ALL_SONGS) { _, oldMode, newMode ->
        if (oldMode != newMode) {
            searchSongs(lastQuery, true)
        }
    }

    var lastQuery = ""

    fun searchSongs(query: String, forceFetch: Boolean = false) {
        if (query == lastQuery && !forceFetch) return

        lastQuery = query
        songsResource.postValue(Resource.Loading())

        currentJob?.cancel()

        songs.clear()

        currentJob = viewModelScope.launch {
            val searchResult = when (searchMode) {
                SearchMode.ALL_SONGS -> searchAllSongs.execute(SearchAllSongs.Params.create(lastQuery, 0))
                SearchMode.ITUNES_SONGS -> searchRemoteSongs.execute(SearchRemoteSongs.Params.create(lastQuery, 0))
                SearchMode.LOCAL_SONGS -> searchLocalSongs.execute(SearchLocalSongs.Params.create(lastQuery, 0))
            }

            searchResult.whenOk {
                hasMoreSongs = this.value.isNotEmpty()
                songs.addAll(this.value.mapToView())
                songsResource.postValue(Resource.Success(songs.distinctBy { it.id }))
            }.whenApiError(errorCode = 403) {
                showToastResource.postValue(Event("Too many requests. Please wait"))
            }.whenError {
                songsResource.postValue(Resource.Error(message = "Something went wrong"))
            }
        }
    }

    fun searchSongsIncremental() {
        if (!hasMoreSongs || currentJob?.isCompleted != true) return

        incrementalProgress.postValue(Event(true))

        currentJob = viewModelScope.launch {
            //TODO ALL_SONGS -- OFFSET
            val searchResult = when (searchMode) {
                SearchMode.ALL_SONGS -> searchAllSongs.execute(SearchAllSongs.Params.create(lastQuery, songs.size))
                SearchMode.ITUNES_SONGS -> searchRemoteSongs.execute(
                    SearchRemoteSongs.Params.create(
                        lastQuery,
                        songs.size
                    )
                )
                SearchMode.LOCAL_SONGS -> searchLocalSongs.execute(
                    SearchLocalSongs.Params.create(
                        lastQuery,
                        songs.size
                    )
                )
            }

            searchResult.whenOk {
                hasMoreSongs = this.value.isNotEmpty()
                songs.addAll(this.value.mapToView())
                songsResource.postValue(Resource.Success(songs.distinctBy { it.id }))
            }
        }
    }

    fun List<Song>.mapToView() = map { mapper.mapToView(it) }
}


enum class SearchMode {
    ITUNES_SONGS, LOCAL_SONGS, ALL_SONGS
}