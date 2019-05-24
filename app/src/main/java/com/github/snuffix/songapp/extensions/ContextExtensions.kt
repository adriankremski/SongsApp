package com.github.snuffix.songapp.extensions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat

fun Context.inflateView(@LayoutRes layoutRes: Int, parent: ViewGroup?, attachToRoot: Boolean = false): View = LayoutInflater.from(this).inflate(layoutRes, parent, attachToRoot)
fun Context.drawableCompat(@DrawableRes drawableRes: Int) = ContextCompat.getDrawable(this, drawableRes)!!
