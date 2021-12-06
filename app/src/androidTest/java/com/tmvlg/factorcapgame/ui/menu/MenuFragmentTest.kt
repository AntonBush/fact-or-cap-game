package com.tmvlg.factorcapgame.ui.menu

import android.content.Context
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tmvlg.factorcapgame.ActionsForTests
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.net.NetworkInfo

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.test.core.app.ApplicationProvider


@RunWith(AndroidJUnit4::class)
class MenuFragmentTest : ActionsForTests() {

    private val statisticsElems = intArrayOf(
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
//        R.id.games_statistics_list
//        R.id.score,
//        R.id.duration,
//        R.id.date
    )

    private val singleGameElems : IntArray = intArrayOf(
        R.id.agree_button,
        R.id.disagree_button,
        R.id.tv_score,
        R.id.tv_fact
    )

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun testGoToStatic() {
        click(R.id.stat_button)
        checkDisplayedAll(*statisticsElems)
    }


    @Test
    fun testGoToSingleGame(){
        click(R.id.single_game_button)
        checkDisplayedAll(*singleGameElems)
    }

    @Test
    fun testNoInternet(){
        assert(isConnected(ApplicationProvider.getApplicationContext()))
    }



fun isConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw      = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected
    }
}

}