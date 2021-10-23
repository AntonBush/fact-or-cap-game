package com.tmvlg.factorcapgame.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.ui.menu.FinishedMenuFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container,FinishedMenuFragment())
            .commit()
    }
}