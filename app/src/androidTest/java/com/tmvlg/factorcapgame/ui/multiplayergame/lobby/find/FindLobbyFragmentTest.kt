package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tmvlg.factorcapgame.BaseTest
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.ui.MainActivity
import com.tmvlg.factorcapgame.ui.menu.MenuFragmentTest.Companion.menuElems
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FindLobbyFragmentTest : BaseTest(){


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

    }

    @Test
    fun elementsFindLobbyIsDisplayed(){
        checkDisplayedAll(*findLobbyElems)
    }

    @Test
    fun goToMenu(){
        click(R.id.return_button)
        checkDisplayedAll(*menuElems)
    }

    @Test
    fun inviteToRoom(){

    }


    companion object {
        val findLobbyElems = intArrayOf(
            R.id.tv_join_lobby,
            R.id.find_lobby_edittext,
            R.id.menu_button,
            R.id.join_button,
            R.id.return_button
        )
    }
}