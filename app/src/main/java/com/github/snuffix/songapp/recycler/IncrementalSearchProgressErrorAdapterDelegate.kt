package com.github.snuffix.songapp.recycler

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.snuffix.songapp.R
import com.github.snuffix.songapp.extensions.inflateView
import com.github.snuffix.songapp.songs.adapter.ViewItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import kotlinx.android.synthetic.main.item_error_row.view.*

class IncrementalSearchProgressErrorAdapterDelegate(private val onRetry: () -> Unit) : AdapterDelegate<List<ViewItem>>() {

    override fun onBindViewHolder(
        items: List<ViewItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>
    ) {
        val error = items[position] as ProgressError


        if (error.show) {
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
            holder.itemView.errorView.error(error.message)
        } else {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }

    override fun isForViewType(items: List<ViewItem>, position: Int) = items[position] is ProgressError

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val itemView = parent.context.inflateView(R.layout.item_error_row, parent)
        itemView.errorView.onRetry = onRetry
        return object : RecyclerView.ViewHolder(itemView) {}
    }
}

class ProgressError(var show: Boolean = false, var message: String = "") : ViewItem

