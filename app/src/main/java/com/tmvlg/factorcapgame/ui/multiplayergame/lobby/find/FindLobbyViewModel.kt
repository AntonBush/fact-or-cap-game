package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import android.util.Log
import androidx.lifecycle.*
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class FindLobbyViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val userRepository: UserRepository
): ViewModel() {
    val lobbies = firebaseLobbyRepository.lobbies.map { it }

    val connectedLobby = MutableLiveData<Lobby?>(null)

    fun listenLobbies() = viewModelScope.launch {
        firebaseLobbyRepository.listenLobbies()
    }

    fun stopListenLobbies() = viewModelScope.launch {
        firebaseLobbyRepository.stopListenLobbies()
    }

    fun connectLobby(selectedLobbies: List<Lobby>) = viewModelScope.launch {
        if (selectedLobbies.isEmpty()) {
            return@launch
        }

        // TODO("Connection to lobby")

        connectedLobby.postValue(selectedLobbies.first())
    }
}