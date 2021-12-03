package com.tmvlg.factorcapgame.ui.multiplayergame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.game.GameRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository

class MultiplayerGameViewModelFactory(
    private val gameRepository: GameRepository,
    private val factRepository: FactRepository,
    private val userRepository: UserRepository,
    private val firebaseLobbyRepository: FirebaseLobbyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return try {
            modelClass.getConstructor(
                GameRepository::class.java,
                FactRepository::class.java,
                UserRepository::class.java,
                FirebaseLobbyRepository::class.java
            ).newInstance(
                gameRepository,
                factRepository,
                userRepository,
                firebaseLobbyRepository
            )
        } catch (e: Exception) {
            val exception = IllegalArgumentException("Unknown view model class $modelClass")
            exception.addSuppressed(e)
            throw exception
        }
    }
}
