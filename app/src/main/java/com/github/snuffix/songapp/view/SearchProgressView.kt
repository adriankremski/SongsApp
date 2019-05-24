package com.github.snuffix.songapp.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.snuffix.songapp.R
import com.github.snuffix.songapp.extensions.drawableCompat
import kotlinx.android.synthetic.main.item_error_view.view.*

class SearchProgressView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.item_search_progress, this, true)
    }
}