package com.github.snuffix.songapp.view

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.snuffix.songapp.R
import com.github.snuffix.songapp.extensions.drawableCompat
import kotlinx.android.synthetic.main.item_error_view.view.*


class ErrorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.item_error_view, this, true)
    }

    fun networkError() {
        errorImage.setBackgroundResource(R.drawable.network_error_animation)
        (errorImage.background as? AnimationDrawable)?.start()
        errorMessageLabel.text = context.getString(R.string.no_internet_connection)
    }

    fun error(message: String? = null) {
        errorImage.setBackgroundResource(0)
        errorImage.setImageDrawable(context.drawableCompat(R.drawable.ic_error))
        errorMessageLabel.text = message ?: context.getString(R.string.something_went_wrong)
    }
}