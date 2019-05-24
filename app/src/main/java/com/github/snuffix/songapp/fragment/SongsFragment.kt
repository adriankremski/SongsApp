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
import com.github.snuffix.songapp.fragment.songs.adapter.SongsAdapter
import com.github.snuffix.songapp.mapper.SongsMapper
import com.github.snuffix.songapp.presentation.SearchMode
import com.github.snuffix.songapp.presentation.SongsViewModel
import com.github.snuffix.songapp.presentation.model.ErrorType
import com.github.snuffix.songapp.recycler.decoration.VerticalSpaceItemDecoration
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
        R.id.all_songs to SearchMode.ALL_SONGS,
        R.id.local_songs to SearchMode.LOCAL_SONGS,
        R.id.itunes_songs to SearchMode.ITUNES_SONGS
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

        searchView.isActivated = true
        searchView.queryHint = getString(R.string.hint_search_songs)
        searchView.onActionViewExpanded()
        searchView.setOnQueryTextListener(DebouncingQueryTextListener(lifecycle) { query ->
            songsViewModel.searchSongs(query)
        })

        songsViewModel.showToast.observe {
            it.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        songsViewModel.showIncrementalProgress.observe {
            it.getContentIfNotHandled()?.let { show ->
                songsAdapter.showIncrementalProgress(show)
                songsAdapter.notifyDataSetChanged()
            }
        }

        songsViewModel.showSearchProgress.observe {
            it.getContentIfNotHandled()?.let { show ->
                if (show) {
                    searchProgress.clearAnimation()
                    searchProgress.alpha = 1f
                    searchProgress.visibility = View.VISIBLE
                } else {
                    if (songsAdapter.itemCount == 0) {
                        searchProgress.visibility = View.GONE
                    } else {
                        searchProgress.animate().alpha(0f).withEndAction { searchProgress.visibility = View.GONE }.duration = 250
                    }
                }
            }
        }

        songsViewModel.songsData.observe(
            onLoading = {
                errorView.visibility = View.GONE
                emptyListMessage.visibility = View.GONE
            },
            onError = {
                errorView.visibility = View.VISIBLE

                if (it.errorType == ErrorType.NETWORK) {
                    errorView.networkError()
                } else {
                    errorView.error(it.message)
                }
            },
            onSuccess = { resource ->
                val items = resource.data.map { songsMapper.mapToUIModel(it) }

                if (items.isEmpty()) {
                    emptyListMessage.visibility = View.VISIBLE
                }

                songsAdapter.items = items
                songsAdapter.notifyDataSetChanged()
            }
        )

        songsRecycler.addItemDecoration(VerticalSpaceItemDecoration(requireContext().resources.getDimensionPixelSize(R.dimen.spacing_normal)))

        LinearLayoutManager(requireContext()).apply {
            songsRecycler.layoutManager = this
            songsRecycler.addOnScrollListener(RecyclerViewBottomScrollListener(this) {
                songsViewModel.searchSongsIncremental()
            })
        }

        songsRecycler.adapter = SongsAdapter()
    }

    override fun onPause() {
        super.onPause()
        searchProgress.clearAnimation()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        val selectedItemId = searchModeToIdMapping.entries.first { it.value == songsViewModel.searchMode }.key
        menu?.findItem(selectedItemId)?.isChecked = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_songs, menu)
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