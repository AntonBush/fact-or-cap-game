package com.tmvlg.factorcapgame

import android.app.Application
import com.tmvlg.factorcapgame.data.FactOrCapDatabase
import com.tmvlg.factorcapgame.data.preferences.PreferenceProvider
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseGameRepository
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.game.GameRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository

class FactOrCapApplication : Application() {
    val database by lazy { FactOrCapDatabase.getDatabase(this) }
    val gameRepository by lazy { GameRepository(database.gameDao()) }
    val factRepository by lazy { FactRepository.newInstance() }

    val preferenceProvider by lazy { PreferenceProvider(this) }

    val userRepository by lazy { UserRepository(preferenceProvider) }

    val firebaseGameRepository by lazy { FirebaseGameRepository() }
    val firebaseLobbyRepository by lazy { FirebaseLobbyRepository() }
}
