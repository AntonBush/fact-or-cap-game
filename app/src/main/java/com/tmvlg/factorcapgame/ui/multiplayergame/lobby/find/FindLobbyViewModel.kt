package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import androidx.lifecycle.*
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class FindLobbyViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val lobbies = firebaseLobbyRepository.lobbies.map { it }

    val connectedLobbyIdAndPlayerId =
        MutableLiveData<Pair<String, String>?>(null)

    fun listenLobbies() = viewModelScope.launch {
        firebaseLobbyRepository.listenLobbies()
    }

    fun stopListenLobbies() = viewModelScope.launch {
        firebaseLobbyRepository.stopListenLobbies()
    }

    fun connectLobby(selectedLobby: Lobby) = viewModelScope.launch {
        val playerId = firebaseLobbyRepository.addPlayerToLobby(
            userRepository.getUsername()!!,
            selectedLobby.id
        )
        connectedLobbyIdAndPlayerId.postValue(selectedLobby.id to playerId)
    }
}