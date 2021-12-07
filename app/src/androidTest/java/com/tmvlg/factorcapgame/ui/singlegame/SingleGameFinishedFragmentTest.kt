package com.tmvlg.factorcapgame.ui.singlegame

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
class SingleGameFinishedFragmentTest : BaseTest() {


    private val singleGameFinishedElems : IntArray = intArrayOf(
        R.id.tv_gamescore,
        R.id.tv_score_points,
        R.id.tv_highscore,
        R.id.restart_button,
        R.id.menu_button
    )

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)


    @Before
    fun setup() {
        activityRule.scenario.onActivity {
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, SingleGameFinishedFragment.newInstance(100, true))
                .commit()
        }

    }

    @Test
    fun testDisplayedFinishSingleGame() {
        checkDisplayedAll(*singleGameFinishedElems)
    }

    @Test
    fun testGoToMenu(){
        click(R.id.menu_button)
        checkDisplayedAll(*menuElems)
    }

    @Test
    fun testClickPlayAgain(){
        click(R.id.restart_button)
        checkDisplayedAll(*singleGameElems)
    }

    private val singleGameElems : IntArray = intArrayOf(
        R.id.agree_button,
        R.id.disagree_button,
        R.id.tv_score,
        R.id.tv_fact
    )
}
