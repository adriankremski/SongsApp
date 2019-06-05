package com.github.snuffix.songapp.songs

import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.github.snuffix.songapp.BaseFragment
import com.github.snuffix.songapp.MainActivity
import com.github.snuffix.songapp.R
import com.github.snuffix.songapp.extensions.setVisible
import com.github.snuffix.songapp.mapper.SongsMapper
import com.github.snuffix.songapp.presentation.SearchSource
import com.github.snuffix.songapp.presentation.SongsViewModel
import com.github.snuffix.songapp.presentation.model.ErrorType
import com.github.snuffix.songapp.songs.adapter.SongsAdapter
import com.github.snuffix.songapp.utils.DebouncingQueryTextListener
import kotlinx.android.synthetic.main.fragment_songs.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SongsFragment : BaseFragment() {

    private val songsMapper by inject<SongsMapper>()
    private val songsViewModel by viewModel<SongsViewModel>()

    private val searchModeToIdMapping = mapOf(
        R.id.all_songs to SearchSource.ALL_SONGS,
        R.id.local_songs to SearchSource.LOCAL_SONGS,
        R.id.itunes_songs to SearchSource.REMOTE_SONGS
    )

    private val songsAdapter: SongsAdapter
        get() = songsRecycler.adapter as SongsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_songs, container, false)

        setHasOptionsMenu(true)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.attachToolbar(getString(R.string.screen_title_songs), toolbar)

        initSearchView()

        songsRecycler.onBottomReached = { songsViewModel.searchSongsIncremental() }

        songsRecycler.adapter = SongsAdapter {
            songsViewModel.searchSongsIncremental()
        }

        subscribeToSongs()

        songsViewModel.tooManyRequestsToast().observe {
            Toast.makeText(requireContext(), "Too many requests. Please wait", Toast.LENGTH_LONG).show()
        }

        searchErrorView.onRetry = {
            songsViewModel.searchSongs(forceFetch = true)
        }
    }

    private fun initSearchView() {
        searchView.isActivated = true
        searchView.queryHint = getString(R.string.hint_search_songs)
        searchView.onActionViewExpanded()
        searchView.setOnQueryTextListener(DebouncingQueryTextListener(lifecycle) { query ->
            songsViewModel.searchSongs(query)
        })
    }

    private fun subscribeToSongs() {
        songsViewModel.songsData().observe(
            onLoading = {
                searchErrorView.visibility = View.GONE
                emptyView.visibility = View.GONE

                if (songsViewModel.isIncrementalSearch) {
                    songsAdapter.showIncrementalProgress(true)
                    songsAdapter.showIncrementalError(false)
                    songsAdapter.notifyDataSetChanged()
                } else {
                    searchProgress.show()
                }
            },
            onError = { message, errorType ->
                if (songsViewModel.isIncrementalSearch) {
                    onIncrementalSearchError(message = message, errorType = errorType)
                } else {
                    onSearchError(message = message, errorType = errorType)
                }
            },
            onSuccess = { songs ->
                val songs = songs.map { songsMapper.mapToUIModel(it) }

                emptyView.setVisible(songs.isEmpty())
                searchErrorView.visibility = View.GONE
                searchProgress.hide(animate = songs.isNotEmpty())
                songsAdapter.items = songs
                songsAdapter.showIncrementalProgress(false)
                songsAdapter.showIncrementalError(false)
                songsAdapter.notifyDataSetChanged()
            }
        )
    }

    private fun onIncrementalSearchError(message: String? = null, errorType: ErrorType) {
        songsAdapter.showIncrementalProgress(false)

        if (errorType == ErrorType.NETWORK) {
            songsAdapter.showIncrementalError(true, getString(R.string.no_internet_connection))
        } else {
            songsAdapter.showIncrementalError(true, "Too many requests")
        }

        songsAdapter.notifyDataSetChanged()
    }

    private fun onSearchError(message: String? = null, errorType: ErrorType) {
        songsAdapter.items = listOf()
        searchProgress.hide(animate = false)
        songsAdapter.notifyDataSetChanged()

        searchErrorView.visibility = View.VISIBLE

        if (errorType == ErrorType.NETWORK) {
            searchErrorView.networkError()
        } else {
            searchErrorView.error(message)
        }
    }

    override fun onPause() {
        super.onPause()
        searchProgress.clearAnimation()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_songs, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val selectedItemId = searchModeToIdMapping.entries.first { it.value == songsViewModel.searchSource }.key
        menu.findItem(selectedItemId)?.isChecked = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!item.isChecked) {
            item.isChecked = true
        }

        return if (searchModeToIdMapping.containsKey(item.itemId)) {
            songsViewModel.searchSource = searchModeToIdMapping.getValue(item.itemId)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}