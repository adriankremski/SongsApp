package com.github.snuffix.songapp

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun attachToolbar(title: String? = null, toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navHostFragment.findNavController())
        supportActionBar?.title = title
    }
}
