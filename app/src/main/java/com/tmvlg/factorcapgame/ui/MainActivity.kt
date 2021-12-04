package com.tmvlg.factorcapgame.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.ui.menu.MenuFragment
import com.tmvlg.factorcapgame.ui.menu.MenuViewModel

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MenuViewModel
    lateinit var username: String
    lateinit var snapSE: MediaPlayer
    lateinit var correctSE: MediaPlayer
    lateinit var wrongSE: MediaPlayer
    var soundEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        snapSE = MediaPlayer.create(this, R.raw.click_s_e)
        correctSE = MediaPlayer.create(this, R.raw.correct_answer_s_e)
        wrongSE = MediaPlayer.create(this, R.raw.wrong_answer_s_e)
        // Set MenuFragment as first
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, MenuFragment())
            .commit()

    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        val x = event?.x
//        val y = event?.y
//
//        return super.onTouchEvent(event)
//    }
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
        // uncomment to close your app on back button pressed
//        else {
//            super.onBackPressed()
//        }
    }
}
