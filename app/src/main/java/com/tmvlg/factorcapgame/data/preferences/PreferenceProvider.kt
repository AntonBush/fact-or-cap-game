package com.tmvlg.factorcapgame.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.tmvlg.factorcapgame.data.repository.user.Statistics

class PreferenceProvider(context: Context) {
    private val appContext = context.applicationContext
    private val preference: SharedPreferences
        get() = appContext.getSharedPreferences(APP_USER_PREFERENCES, 0)

    // saves statistics to shared pref
    fun saveStatistics(statistics: Statistics) {
        preference.edit().putInt(
            KEY_TOTAL_GAMES,
            statistics.totalGames
        ).putInt(
            KEY_HIGHEST_SCORE,
            statistics.highestScore
        ).putInt(
            KEY_LAST_SCORE,
            statistics.lastScore
        ).putInt(
            KEY_AVERAGE_SCORE,
            statistics.averageScore
        ).putInt(
            KEY_ALL_SCORES,
            statistics.allScores
        ).apply()
    }

    // loads statistics from shared pref
    fun getStatistics(): Statistics {

        val totalGames = preference.getInt(
            KEY_TOTAL_GAMES,
            0
        )
        val highestScore = preference.getInt(
            KEY_HIGHEST_SCORE,
            0
        )
        val lastScore = preference.getInt(
            KEY_LAST_SCORE,
            0
        )
        val averageScore = preference.getInt(
            KEY_AVERAGE_SCORE,
            0
        )
        val allScores = preference.getInt(
            KEY_ALL_SCORES,
            0
        )
        return Statistics(
            totalGames,
            highestScore,
            lastScore,
            averageScore,
            allScores
        )
    }

    companion object {
        const val APP_USER_PREFERENCES = "userprefs"
        const val KEY_TOTAL_GAMES = "keyTotalGames"
        const val KEY_HIGHEST_SCORE = "keyHighestScore"
        const val KEY_LAST_SCORE = "keyLastScore"
        const val KEY_AVERAGE_SCORE = "keyAverageScore"
        const val KEY_ALL_SCORES = "keyAllScores"
    }
}
