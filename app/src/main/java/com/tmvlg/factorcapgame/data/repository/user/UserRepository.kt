package com.tmvlg.factorcapgame.data.repository.user

import com.tmvlg.factorcapgame.data.preferences.PreferenceProvider

class UserRepository(
    private val preferenceProvider: PreferenceProvider
) {

    fun saveGame(statistics: Statistics) {
        preferenceProvider.saveStatistics(statistics)
    }

    fun getStats(): Statistics {
        return preferenceProvider.getStatistics()
    }
}
