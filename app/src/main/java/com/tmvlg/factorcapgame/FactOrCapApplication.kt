package com.tmvlg.factorcapgame

import android.app.Application
import com.tmvlg.factorcapgame.data.FactOrCapDatabase
import com.tmvlg.factorcapgame.data.preferences.PreferenceProvider
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.game.GameRepositoryImpl
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import com.tmvlg.factorcapgame.ui.singlegame.SingleGameViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class FactOrCapApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { FactOrCapDatabase.getDatabase(this, applicationScope) }
    val gameRepository by lazy { GameRepositoryImpl(database.gameDao()) }

    val factRepository by lazy { FactRepository.newInstance() }

    val preferenceProvider by lazy { PreferenceProvider(this) }

    val userRepository by lazy { UserRepository(preferenceProvider) }

}
