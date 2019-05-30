package com.github.snuffix.songapp.songs.adapter

import com.github.snuffix.songapp.recycler.IncrementalSearchProgressAdapterDelegate
import com.github.snuffix.songapp.recycler.IncrementalSearchProgressErrorAdapterDelegate
import com.github.snuffix.songapp.recycler.ProgressError
import com.github.snuffix.songapp.recycler.SearchProgress
import com.github.snuffix.songapp.utils.Constants
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class SongsAdapter(onRetry: () -> Unit = {}) : ListDelegationAdapter<List<ViewItem>>() {

    private val progress = SearchProgress()
    private val error = ProgressError()

    init {
        delegatesManager.addDelegate(Constants.Adapter.SONG_VIEW_ITEM, SongsAdapterDelegate())
        delegatesManager.addDelegate(Constants.Adapter.PROGRESS_VIEW_ITEM, IncrementalSearchProgressAdapterDelegate())
        delegatesManager.addDelegate(Constants.Adapter.ERROR_VIEW_ITEM, IncrementalSearchProgressErrorAdapterDelegate(onRetry))
    }

    override fun setItems(items: List<ViewItem>) {
        val mutableList = items.toMutableList()
        mutableList.add(progress)
        mutableList.add(error)
        super.setItems(mutableList)
    }

    fun showIncrementalProgress(show: Boolean) {
        progress.show = show
    }

    fun showIncrementalError(show: Boolean, message: String = "") {
        error.show = show
        error.message = message
    }
}