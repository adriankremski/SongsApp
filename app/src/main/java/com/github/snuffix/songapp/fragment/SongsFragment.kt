package com.github.snuffix.songapp.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.github.snuffix.songapp.BR
import com.github.snuffix.songapp.BaseFragment
import com.github.snuffix.songapp.MainActivity
import com.github.snuffix.songapp.R
import com.github.snuffix.songapp.databinding.FragmentSongsBinding
import com.github.snuffix.songapp.extensions.setVisible
import com.github.snuffix.songapp.fragment.songs.adapter.SongsAdapter
import com.github.snuffix.songapp.mapper.SongsMapper
import com.github.snuffix.songapp.presentation.SearchSource
import com.github.snuffix.songapp.presentation.SongsViewModel
import com.github.snuffix.songapp.presentation.model.ErrorType
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
        val viewDataBinding: FragmentSongsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_songs, container, false)

        viewDataBinding.setVariable(BR.viewmodel, songsViewModel)

        setHasOptionsMenu(true)

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.attachToolbar(getString(R.string.screen_title_songs), toolbar)

        initSearchView()

        songsRecycler.onBottomReached = { songsViewModel.searchSongsIncremental() }
        songsRecycler.adapter = SongsAdapter()

        subscribeToSongs()
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
                errorView.visibility = View.GONE
                emptyView.visibility = View.GONE

                if (songsViewModel.isIncrementalSearch) {
                    songsAdapter.showIncrementalProgress(true)
                    songsAdapter.notifyDataSetChanged()
                } else {
                    searchProgress.show()
                }
            },
            onError = {
                if (songsViewModel.isIncrementalSearch) {
                    onIncrementalSearchError(message = it.message, errorType = it.errorType)
                } else {
                    onSearchError(message = it.message, errorType = it.errorType)
                }
            },
            onSuccess = { resource ->
                val songs = resource.data.map { songsMapper.mapToUIModel(it) }

                emptyView.setVisible(songs.isEmpty())
                errorView.visibility = View.GONE
                searchProgress.hide(animate = songs.isNotEmpty())
                songsAdapter.items = songs
                songsAdapter.showIncrementalProgress(false)
                songsAdapter.notifyDataSetChanged()
            }
        )
    }

    private fun onIncrementalSearchError(message: String? = null, errorType: ErrorType) {
        songsAdapter.showIncrementalProgress(false)
        songsAdapter.notifyDataSetChanged()

        if (errorType == ErrorType.NETWORK) {
            Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, message ?: getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
        }
    }

    private fun onSearchError(message: String? = null, errorType: ErrorType) {
        songsAdapter.items = listOf()
        searchProgress.hide(animate = false)
        songsAdapter.notifyDataSetChanged()

        errorView.visibility = View.VISIBLE

        if (errorType == ErrorType.NETWORK) {
            errorView.networkError()
        } else {
            errorView.error(message)
        }
    }

    override fun onPause() {
        super.onPause()
        searchProgress.clearAnimation()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_songs, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        val selectedItemId = searchModeToIdMapping.entries.first { it.value == songsViewModel.searchSource }.key
        menu?.findItem(selectedItemId)?.isChecked = true
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