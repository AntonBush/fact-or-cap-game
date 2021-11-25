package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import android.util.Log
import androidx.lifecycle.*
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class FindLobbyViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val userRepository: UserRepository
): ViewModel() {
    val lobbies = firebaseLobbyRepository.lobbies.map { it }

    fun listenLobbies() = viewModelScope.launch {
        firebaseLobbyRepository.listenLobbies()
    }

    fun stopListenLobbies() = viewModelScope.launch {
        firebaseLobbyRepository.stopListenLobbies()
    }
}