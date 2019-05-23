package com.github.snuffix.songapp.recycler.songs

import androidx.recyclerview.widget.DiffUtil
import com.github.snuffix.songapp.recycler.ViewItem
import com.github.snuffix.songapp.utils.Constants
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter


class SongsAdapter(callback: DiffUtil.ItemCallback<ViewItem>) : AsyncListDifferDelegationAdapter<ViewItem>(callback) {

    private val progress = Progress()

    init {
        delegatesManager.addDelegate(Constants.Adapter.SONG_VIEW_ITEM, SongsAdapterDelegate())
        delegatesManager.addDelegate(Constants.Adapter.PROGRESS_VIEW_ITEM, ProgressAdapterDelegate())
    }

    override fun setItems(items: MutableList<ViewItem>?) {
        items?.let { it.add(progress) }
        super.setItems(items)
    }

    fun showProgress() {
        progress.show = true
    }

    fun hideProgress() {
        progress.show = false
    }
}