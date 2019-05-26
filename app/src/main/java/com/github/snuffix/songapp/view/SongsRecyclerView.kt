package com.github.snuffix.songapp.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.snuffix.songapp.utils.RecyclerViewBottomScrollListener


class SongsRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {

    var onBottomReached: () -> Unit = {}

    init {
        LinearLayoutManager(context).apply {
            layoutManager = this
            addOnScrollListener(RecyclerViewBottomScrollListener(this) { onBottomReached() })
        }
    }
}