package com.tmvlg.factorcapgame.ui.multiplayergame

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tmvlg.factorcapgame.BaseTest
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.ui.MainActivity
import com.tmvlg.factorcapgame.ui.menu.MenuFragmentTest.Companion.menuElems
import com.tmvlg.factorcapgame.ui.statisitics.StatisticsFragment
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class MultiplayerGameFinishedTest : BaseTest(){


    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)


    @Before
    fun setup() {
        activityRule.scenario.onActivity {
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, MultiplayerGameFinished.newInstance(13,"2313"))
                .commit()
        }

    }

    @Test
    fun elementsFinishedMiltiplayerGameIsDisplayed(){
        checkDisplayedAll(*gameFinishedElems)
    }

    @Test
    fun goToMenu(){
        Thread.sleep(10000)
        click(R.id.menu_button)
        checkDisplayedAll(*menuElems)
    }


    @Test
    fun goToLobby(){
        click(R.id.lobby)
    }



    companion object {
        val gameFinishedElems = intArrayOf(
            R.id.tv_game_result,
            R.id.find_lobby_button,
            R.id.menu_button
        )
   }
}