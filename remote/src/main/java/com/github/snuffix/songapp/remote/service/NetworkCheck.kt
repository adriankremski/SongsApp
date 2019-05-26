package com.github.snuffix.songapp.remote.service

interface NetworkCheck {
    fun isOnline(): Boolean
}