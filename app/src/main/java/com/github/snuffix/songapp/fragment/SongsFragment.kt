package com.github.snuffix.songapp.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.github.snuffix.songapp.recycler.VerticalSpaceItemDecoration
import com.github.snuffix.songapp.utils.DebouncingQueryTextListener
import com.github.snuffix.songapp.utils.RecyclerViewBottomScrollListener
import kotlinx.android.synthetic.main.fragment_songs.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SongsFragment : BaseFragment() {

    private val songsMapper by inject<SongsMapper>()
    private val songsViewModel by viewModel<SongsViewModel>()

    private val songsAdapter: SongsAdapter
        get() = songsRecycler.adapter as SongsAdapter

    private val searchModeToIdMapping = mapOf(
        R.id.all_songs to SearchSource.ALL_SONGS,
        R.id.local_songs to SearchSource.LOCAL_SONGS,
        R.id.itunes_songs to SearchSource.ITUNES_SONGS
    )

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
        initSongsRecycler()
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

    private fun initSongsRecycler() {
        songsRecycler.addItemDecoration(VerticalSpaceItemDecoration(requireContext().resources.getDimensionPixelSize(R.dimen.spacing_normal)))

        LinearLayoutManager(requireContext()).apply {
            songsRecycler.layoutManager = this
            songsRecycler.addOnScrollListener(RecyclerViewBottomScrollListener(this) {
                songsViewModel.searchSongsIncremental()
            })
        }

        songsRecycler.adapter = SongsAdapter()
    }

    private fun subscribeToSongs() {
        songsViewModel.songsData.observe(
            onLoading = {
                errorView.visibility = View.GONE
                emptyView.visibility = View.GONE

                if (songsViewModel.isIncrementalSearch) {
                    songsAdapter.showIncrementalProgress(true)
                    songsAdapter.notifyDataSetChanged()
                } else {
                    searchProgress.clearAnimation()
                    searchProgress.alpha = 1f
                    searchProgress.visibility = View.VISIBLE
                }
            },
            onError = {
                if (songsViewModel.isIncrementalSearch) {
                    songsAdapter.showIncrementalProgress(false)
                    songsAdapter.notifyDataSetChanged()

                    if (it.errorType == ErrorType.NETWORK) {
                        Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, it.message ?: getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
                    }
                } else {
                    hideSearchProgress()

                    errorView.visibility = View.VISIBLE

                    if (it.errorType == ErrorType.NETWORK) {
                        errorView.networkError()
                    } else {
                        errorView.error(it.message)
                    }
                }
            },
            onSuccess = { resource ->
                hideSearchProgress()

                val songs = resource.data.map { songsMapper.mapToUIModel(it) }
                emptyView.setVisible(songs.isEmpty())

                songsAdapter.items = songs
                songsAdapter.showIncrementalProgress(false)
                songsAdapter.notifyDataSetChanged()
            }
        )
    }

    private fun hideSearchProgress() {
        if (songsAdapter.itemCount == 0) {
            searchProgress.visibility = View.GONE
        } else {
            searchProgress.animate().alpha(0f).withEndAction { searchProgress.visibility = View.GONE }.duration = 250
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
        val selectedItemId = searchModeToIdMapping.entries.first { it.value == songsViewModel.searchMode }.key
        menu?.findItem(selectedItemId)?.isChecked = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!item.isChecked) {
            item.isChecked = true
        }

        return if (searchModeToIdMapping.containsKey(item.itemId)) {
            songsViewModel.searchMode = searchModeToIdMapping.getValue(item.itemId)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}