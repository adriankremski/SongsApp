package com.github.snuffix.songapp.recycler.songs

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.snuffix.songapp.R
import com.github.snuffix.songapp.extensions.inflateView
import com.github.snuffix.songapp.recycler.ViewItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

class ProgressAdapterDelegate : AdapterDelegate<List<ViewItem>>() {

    override fun onBindViewHolder(
        items: List<ViewItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>
    ) {
        val visibility = if ((items[position] as Progress).show) View.VISIBLE else View.GONE
        holder.itemView.visibility = visibility
    }

    override fun isForViewType(items: List<ViewItem>, position: Int) = items[position] is Progress

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val itemView = parent.context.inflateView(R.layout.item_progress_row, parent)
        return object : RecyclerView.ViewHolder(itemView) {}
    }
}

class Progress(var show: Boolean = false) : ViewItem