package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository

class FindLobbyViewModelFactory(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val userRepository: UserRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FindLobbyViewModel::class.java))
            return FindLobbyViewModel(firebaseLobbyRepository, userRepository) as T
        throw IllegalArgumentException("Unknown view model class $modelClass")
    }
}
