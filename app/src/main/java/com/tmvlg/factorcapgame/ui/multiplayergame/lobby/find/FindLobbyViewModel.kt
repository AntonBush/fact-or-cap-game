package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class FindLobbyViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val lobbies = firebaseLobbyRepository.lobbyList.map { it }

    val connectedLobbyId =
        MutableLiveData<String?>(null)

    fun listenLobbies() = viewModelScope.launch {
        firebaseLobbyRepository.listenLobbies()
    }

    fun stopListenLobbies() = viewModelScope.launch {
        firebaseLobbyRepository.stopListenLobbies()
    }

    fun connectLobby(selectedLobby: Lobby) = viewModelScope.launch {
        firebaseLobbyRepository.addPlayerToLobby(
            userRepository.getUsername()!!,
            selectedLobby.id
        )
        connectedLobbyId.postValue(selectedLobby.id)
    }
}
