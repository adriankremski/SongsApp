package com.github.snuffix.songapp.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewBottomScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val onBottomReached: () -> Unit
) : RecyclerView.OnScrollListener() {

    private val refreshThreshold: Int = 5
    private var haveScrolled: Boolean = false

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        // This is just to be fail safe so it triggers bottomReached even if RV has less items than its physical length.
        if (newState == RecyclerView.SCROLL_STATE_IDLE && haveReachedBottom() && !haveScrolled) {
            onBottomReached()
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

        if (dy <= 0) {
            return
        }

        haveScrolled = true

        if (haveReachedBottom()) {
            onBottomReached()
        }
    }

    private fun haveReachedBottom(): Boolean {
        val totalItemCount = layoutManager.itemCount
        val lastPos = layoutManager.findLastVisibleItemPosition()
        val firstPos = layoutManager.findFirstVisibleItemPosition()

        if (lastPos < 0) {
            return false
        }

        if (firstPos == 0 && lastPos == totalItemCount - 1) {
            return false
        }

        return lastPos > totalItemCount - refreshThreshold

    }
}