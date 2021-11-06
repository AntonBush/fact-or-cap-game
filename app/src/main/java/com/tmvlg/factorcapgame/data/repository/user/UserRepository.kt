package com.tmvlg.factorcapgame.data.repository.user

import androidx.annotation.WorkerThread
import com.tmvlg.factorcapgame.data.preferences.PreferenceProvider

class UserRepository(
    private val preferenceProvider: PreferenceProvider
) {
    @WorkerThread
    fun saveGame(statistics: Statistics) {
        preferenceProvider.saveStatistics(statistics)
    }

    @WorkerThread
    fun getStats(): Statistics {
        return preferenceProvider.getStatistics()
    }
}
