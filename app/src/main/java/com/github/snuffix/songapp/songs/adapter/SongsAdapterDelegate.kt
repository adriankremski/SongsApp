package com.github.snuffix.songapp.songs.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.snuffix.songapp.R
import com.github.snuffix.songapp.extensions.inflateView
import com.github.snuffix.songapp.model.Song
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import kotlinx.android.synthetic.main.item_song_row.view.*
import java.text.DateFormat


class SongsAdapterDelegate : AbsListItemAdapterDelegate<Song, ViewItem, SongsAdapterDelegate.SongItemHolder>() {

    override fun onBindViewHolder(item: Song, holder: SongItemHolder, payloads: MutableList<Any>) {
        holder.bind(item)
    }

    override fun isForViewType(item: ViewItem, items: MutableList<ViewItem>, position: Int) = item is Song

    override fun onCreateViewHolder(parent: ViewGroup): SongItemHolder {
        val itemView = parent.context.inflateView(R.layout.item_song_row, parent)
        return SongItemHolder(itemView)
    }

    inner class SongItemHolder(private val childView: View) : RecyclerView.ViewHolder(childView) {

        private val context: Context
            get() = childView.context

        fun bind(song: Song) {
            childView.trackNameLabel.text = song.trackName
            childView.artistNameLabel.text = song.artistName

            val formattedDate = when {
                song.releaseDate != null -> DateFormat.getDateInstance().format(song.releaseDate.toDate())
                song.releaseYear != null -> song.releaseYear.toString()
                else -> "----"
            }

            childView.releaseDateLabel.text = formattedDate

            Glide.with(context).clear(childView.trackImage)

            val requestOptions = RequestOptions().placeholder(R.drawable.ic_list_image_placeholder)

            Glide.with(context)
                .load(song.imageUrl ?: "")
                .apply(requestOptions)
                .into(childView.trackImage)
        }
    }
}