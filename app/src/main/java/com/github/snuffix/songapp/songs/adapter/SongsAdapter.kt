package com.github.snuffix.songapp.songs.adapter

import com.github.snuffix.songapp.recycler.SearchProgress
import com.github.snuffix.songapp.recycler.SearchProgressAdapterDelegate
import com.github.snuffix.songapp.utils.Constants
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class SongsAdapter : ListDelegationAdapter<List<ViewItem>>() {

    private val progress = SearchProgress()

    init {
        delegatesManager.addDelegate(Constants.Adapter.SONG_VIEW_ITEM, SongsAdapterDelegate())
        delegatesManager.addDelegate(Constants.Adapter.PROGRESS_VIEW_ITEM, SearchProgressAdapterDelegate())
    }

    override fun setItems(items: List<ViewItem>) {
        val mutableList = items.toMutableList()
        mutableList.add(progress)
        super.setItems(mutableList)
    }

    fun showIncrementalProgress(show: Boolean) {
        progress.show = show
    }
}