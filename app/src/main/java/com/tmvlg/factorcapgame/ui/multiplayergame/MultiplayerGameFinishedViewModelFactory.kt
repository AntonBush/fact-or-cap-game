package com.tmvlg.factorcapgame.ui.multiplayergame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository

class MultiplayerGameFinishedViewModelFactory(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val userRepository: UserRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return try {
            modelClass.getConstructor(
                FirebaseLobbyRepository::class.java,
                UserRepository::class.java
            ).newInstance(firebaseLobbyRepository, userRepository)
        } catch (e: Exception) {
            val exception = IllegalArgumentException("Unknown view model class $modelClass")
            exception.addSuppressed(e)
            throw exception
        }
    }
}
