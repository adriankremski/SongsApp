package com.github.snuffix.songapp.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.github.snuffix.songapp.R

class SearchProgressView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_search_progress, this, true)
    }

    fun show() {
        clearAnimation()
        alpha = 1f
        visibility = View.VISIBLE
    }

    fun hide(animate: Boolean = false) {
        if (animate) {
            animate().alpha(0f).withEndAction { visibility = View.GONE }.duration = 250
        } else {
            visibility = View.GONE
        }
    }
}