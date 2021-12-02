package com.tmvlg.factorcapgame.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.ui.menu.MenuFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.InviteConnectionFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.LobbyFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lobbyId = intent.extras?.getString("lobbyId")

        Log.d("1", "onCreate: arguments = $lobbyId")

        if (lobbyId != null && lobbyId != "") {
//            val lobbyId: String = arguments.getString("lobbyId") ?: ""
            Log.d("1", "onCreate: lobbyid = $lobbyId")
            Toast.makeText(this, lobbyId, Toast.LENGTH_SHORT).show()
            navigateToLobby(lobbyId)
        }
        else {

            // Set MenuFragment as first
            navigateToMenu()
        }

//        // Set MenuFragment as first
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.main_fragment_container, MenuFragment())
//            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
        // uncomment to close your app on back button pressed
//        else {
//            super.onBackPressed()
//        }
    }

    private fun navigateToMenu() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, MenuFragment())
            .commit()
    }

    private fun navigateToLobby(lobbyId: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, InviteConnectionFragment.newInstance(lobbyId))
            .commit()
    }
}
