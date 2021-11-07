package com.tmvlg.factorcapgame.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.ui.menu.MenuFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Set MenuFragment as first
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, MenuFragment())
            .commit()
    }
}
