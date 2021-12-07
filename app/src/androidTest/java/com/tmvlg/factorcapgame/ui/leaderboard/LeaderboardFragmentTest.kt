package com.tmvlg.factorcapgame.ui.leaderboard

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tmvlg.factorcapgame.BaseTest
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.ui.MainActivity
import com.tmvlg.factorcapgame.ui.menu.MenuFragmentTest.Companion.menuElems
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LeaderboardFragmentTest : BaseTest() {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        activityRule.scenario.onActivity {
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, LeaderboardFragment())
                .commit()
        }
    }

    @Test
    fun leaderboardIsDisplayed() {
        Thread.sleep(5000)
        checkDisplayedAll(*leaderboardElems)
    }

    @Test
    fun goToMenu() {
        click(R.id.return_button)
        checkDisplayedAll(*menuElems)
    }

    companion object {
        val leaderboardElems = intArrayOf(
            R.id.leaderboard_header,
            R.id.return_button,
            R.id.leaderboard_refresh_button,
            R.id.games_leaderboard_list
        )
    }
}
