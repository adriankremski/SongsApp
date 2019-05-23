package com.github.snuffix.songapp.recycler

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.snuffix.songapp.R
import com.github.snuffix.songapp.extensions.inflateView
import com.github.snuffix.songapp.fragment.songs.adapter.ViewItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

class SearchProgressAdapterDelegate : AdapterDelegate<List<ViewItem>>() {

    override fun onBindViewHolder(
        items: List<ViewItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>
    ) {
        val visibility = if ((items[position] as SearchProgress).show) View.VISIBLE else View.GONE
        holder.itemView.visibility = visibility
    }

    override fun isForViewType(items: List<ViewItem>, position: Int) = items[position] is SearchProgress

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val itemView = parent.context.inflateView(R.layout.item_progress_row, parent)
        return object : RecyclerView.ViewHolder(itemView) {}
    }
}

class SearchProgress(var show: Boolean = false) : ViewItem

