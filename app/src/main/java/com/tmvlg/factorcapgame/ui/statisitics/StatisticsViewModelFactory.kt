package com.tmvlg.factorcapgame.ui.statisitics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.game.GameRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository

class StatisticsViewModelFactory(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return try {
            modelClass.getConstructor(
                UserRepository::class.java,
                GameRepository::class.java
            ).newInstance(userRepository, gameRepository)
        } catch (e: Exception) {
            val exception = IllegalArgumentException("Unknown view model class $modelClass")
            exception.addSuppressed(e)
            throw exception
        }
    }
}
