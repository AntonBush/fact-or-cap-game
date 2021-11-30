package com.tmvlg.factorcapgame.data.repository.user

import androidx.annotation.WorkerThread
import com.tmvlg.factorcapgame.data.preferences.PreferenceProvider

class UserRepository(
    private val preferenceProvider: PreferenceProvider
) {
    // saves statistics to shared pref
    @WorkerThread
    fun saveGame(statistics: Statistics) {
        preferenceProvider.saveStatistics(statistics)
    }

    // loads statistics to shared pref
    @WorkerThread
    fun getStats(): Statistics {
        return preferenceProvider.getStatistics()
    }

    @WorkerThread
    fun saveUsername(username: String?) {
        preferenceProvider.saveUsername(username)
    }

    @WorkerThread
    fun getUsername(): String? {
        return preferenceProvider.getUsername()
    }

    @WorkerThread
    fun saveToken(token: String?) {
        preferenceProvider.setRegistrationToken(token)
    }

    @WorkerThread
    fun getToken(): String? {
        return preferenceProvider.getRegistrationToken()
    }
}
