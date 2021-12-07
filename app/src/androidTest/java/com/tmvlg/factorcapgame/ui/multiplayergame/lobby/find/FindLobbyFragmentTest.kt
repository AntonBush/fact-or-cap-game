package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tmvlg.factorcapgame.BaseTest
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.ui.MainActivity
import com.tmvlg.factorcapgame.ui.menu.MenuFragmentTest.Companion.menuElems
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class FindLobbyFragmentTest : BaseTest() {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        activityRule.scenario.onActivity {
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, FindLobbyFragment())
                .commit()
        }

        val database = Firebase.database
        val lobbiesRef = database.getReference("Lobbies")
        val newLobby = Lobby(
            hostName = "TEST_ROOM",
            name = "TEST_ROOM"
        )
        val newLobbyKey = lobbiesRef.push().key ?: throw IOException("can't add new lobby")
        lobbiesRef.updateChildren(mapOf(newLobbyKey to newLobby.toMapped()))
    }

    @Test
    fun elementsFindLobbyIsDisplayed() {
        Thread.sleep(10000)
        checkDisplayedAll(*findLobbyElems)
    }

    @Test
    fun goToMenu() {
        click(R.id.return_button)
        checkDisplayedAll(*menuElems)
    }

//    @Test
//    fun inviteToSelectedRoom(){
//        enterData(R.id.find_lobby_edittext, "TEST_ROOM")
//        Thread.sleep(4000)
//        clickOnRecyclerViewItem(R.id.find_lobby_recyclerview)
//        click(R.id.join_button)
//
//    }

    companion object {
        val findLobbyElems = intArrayOf(
            R.id.tv_join_lobby,
            R.id.find_lobby_edittext,
            R.id.join_button,
            R.id.return_button
        )
    }
}
