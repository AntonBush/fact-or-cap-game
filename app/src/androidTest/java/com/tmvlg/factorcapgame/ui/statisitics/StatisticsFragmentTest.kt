package com.tmvlg.factorcapgame.ui.statisitics

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
class StatisticsFragmentTest : BaseTest() {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        activityRule.scenario.onActivity {
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, StatisticsFragment())
                .commit()
        }
    }

    @Test
    fun checkLastGamesRecyclerIsDisplayed() {
        click(R.id.games_toggle_button)
        isDisplayed(R.id.games_statistics_list)
    }

    @Test
    fun goToMenu() {
        click(R.id.return_button)
        checkDisplayedAll(*menuElems)
    }

    companion object {
        val statisticElems = intArrayOf(
            R.id.return_button,
            R.id.statistics_header,
            R.id.total_games_text,
            R.id.total_games_value,
            R.id.highest_score_text,
            R.id.highest_score_value,
            R.id.last_score_text,
            R.id.last_score_value,
            R.id.average_score_text,
            R.id.average_score_value,
            R.id.your_games_header,
            R.id.games_toggle_button,
        )
    }
}
