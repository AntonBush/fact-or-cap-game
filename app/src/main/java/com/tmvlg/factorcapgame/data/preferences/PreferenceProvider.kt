package com.tmvlg.factorcapgame.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.tmvlg.factorcapgame.data.repository.user.Statistics

class PreferenceProvider(context: Context) {
    private val appContext = context.applicationContext
    private val preference: SharedPreferences
        get() = appContext.getSharedPreferences(APP_USER_PREFERENCES, 0)

    fun saveStatistics(statistics: Statistics) {
        preference.edit().putInt(
            KEY_TOTAL_GAMES,
            statistics.total_games
        ).putInt(
            KEY_HIGHEST_SCORE,
            statistics.highest_score
        ).putInt(
            KEY_LAST_SCORE,
            statistics.last_score
        ).putInt(
            KEY_AVERAGE_SCORE,
            statistics.average_score
        ).putInt(
            KEY_ALL_SCORES,
            statistics.all_scores
        ).apply()
    }

    fun getStatistics(): Statistics {

        val total_games = preference.getInt(
            KEY_TOTAL_GAMES,
            0
        )
        val highest_score = preference.getInt(
            KEY_HIGHEST_SCORE,
            0
        )
        val last_score = preference.getInt(
            KEY_LAST_SCORE,
            0
        )
        val average_score = preference.getInt(
            KEY_AVERAGE_SCORE,
            0
        )
        val all_scores = preference.getInt(
            KEY_ALL_SCORES,
            0
        )
        return Statistics(
            total_games,
            highest_score,
            last_score,
            average_score,
            all_scores
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
