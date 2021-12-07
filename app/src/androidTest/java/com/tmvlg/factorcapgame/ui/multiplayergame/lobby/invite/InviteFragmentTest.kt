package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.invite

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tmvlg.factorcapgame.BaseTest
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.ui.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class InviteFragmentTest : BaseTest() {



    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)


    @Before
    fun setup() {
        activityRule.scenario.onActivity {
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, InviteFragment.newInstance("TEST", "TEST"))
                .commit()
        }

    }

//    @Test
//    fun invitePlayer(){
//        Thread.sleep(10000)
//        enterData(R.id.find_users_edittext, "BUSHEVALEX")
//        Thread.sleep(10000)
//        click(R.id.search_button)
//        Thread.sleep (10000)
//        clickOnRecyclerViewItem(R.id.rv_found_users)
//        Thread.sleep(10000)
//        click(R.id.confirm_button)
//        Thread.sleep(10000)
//    }

    @Test
    fun testDisplayedInviteFragmentElements() {
        checkDisplayedAll(*inviteFragmentElems)
    }



    private val inviteFragmentElems : IntArray = intArrayOf(
        R.id.find_users_edittext,
        R.id.return_button,
        R.id.search_button,
        R.id.rv_found_users,
        R.id.tv_invite_friend,
        R.id.confirm_button
    )
}
