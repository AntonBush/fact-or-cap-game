package com.tmvlg.factorcapgame.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.ui.menu.MenuFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.LobbyFragment

class MainActivity : AppCompatActivity() {
    val launcher = FactOrCapAuth.SignInLauncher.newInstance(this)

    lateinit var snapSE: MediaPlayer
    fun snapSEStart() {
        if (soundEnabled) {
            snapSE.start()
        }
    }

    lateinit var correctSE: MediaPlayer
    fun correctSEStart() {
        if (soundEnabled) {
            correctSE.start()
        }
    }

    lateinit var wrongSE: MediaPlayer
    fun wrongSEStart() {
        if (soundEnabled) {
            wrongSE.start()
        }
    }

    var soundEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        snapSE = MediaPlayer.create(this, R.raw.click_s_e)
        correctSE = MediaPlayer.create(this, R.raw.correct_answer_s_e)
        wrongSE = MediaPlayer.create(this, R.raw.wrong_answer_s_e)

        val lobbyId = intent.extras?.getString("lobbyId")

        Log.d("1", "onCreate: arguments = $lobbyId")

        if (lobbyId != null && lobbyId != "") {
            Log.d("1", "onCreate: lobbyid = $lobbyId")
            Toast.makeText(this, lobbyId, Toast.LENGTH_SHORT).show()
            FactOrCapAuth.signIn(this, launcher)
            navigateToLobby(lobbyId)
        } else {

            // Set MenuFragment as first
            navigateToMenu()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
    }

    private fun navigateToMenu() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, MenuFragment())
            .commit()
    }

    private fun navigateToLobby(lobbyId: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, LobbyFragment.newInstance(lobbyId))
            .commit()
    }
}
