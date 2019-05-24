package com.github.snuffix.songapp.fragment.songs.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.snuffix.songapp.R
import com.github.snuffix.songapp.extensions.inflateView
import com.github.snuffix.songapp.model.Song
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import kotlinx.android.synthetic.main.item_song_row.view.*
import java.text.DateFormat

class SongsAdapterDelegate : AdapterDelegate<List<ViewItem>>() {

    override fun onBindViewHolder(
        items: List<ViewItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>
    ) {
        holder as SongItemHolder
        holder.bind(items[position] as Song)
    }

    override fun isForViewType(items: List<ViewItem>, position: Int) = items[position] is Song

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val itemView = parent.context.inflateView(R.layout.item_song_row, parent)
        return SongItemHolder(itemView)
    }

    inner class SongItemHolder(private val childView: View) : RecyclerView.ViewHolder(childView) {
        fun bind(song: Song) {
            childView.trackNameLabel.text = song.trackName
            childView.artistNameLabel.text = song.artistName

            val formattedDate = when {
                song.releaseDate != null -> DateFormat.getDateInstance().format(song.releaseDate.toDate())
                song.releaseYear != null -> song.releaseYear.toString()
                else -> "----"
            }

            childView.releaseDateLabel.text = formattedDate

            if (song.imageUrl != null) {

            } else {

            }
        }
    }
}