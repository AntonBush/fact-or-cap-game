package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import androidx.lifecycle.*
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class LobbyViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private var _username: String? = null
    val username: String
        get() = _username ?: throw IllegalStateException("Username should be loaded")

    init {
        viewModelScope.launch {
            _username = userRepository.getUsername()!!
        }
    }

    val lobby = firebaseLobbyRepository.lobby.map { it }
    val isHost = lobby.map { it?.host == username }

    val _isGameStarted = MutableLiveData(false)
    val isGameStarted = _isGameStarted.map { it }

    fun listenLobby(lobbyId: String) = viewModelScope.launch {
        firebaseLobbyRepository.listenLobby(lobbyId)
    }

    fun stopListenLobby() = viewModelScope.launch {
        firebaseLobbyRepository.stopListenLobby()
    }

    fun startGame() = viewModelScope.launch {
        _isGameStarted.postValue(true)
    }

//    fun connectLobby(selectedLobby: Lobby) = viewModelScope.launch {
//        val playerId = firebaseLobbyRepository.addPlayerToLobby(
//            userRepository.getUsername()!!,
//            selectedLobby.id
//        )
//        connectedLobbyIdAndPlayerId.postValue(selectedLobby.id to playerId)
//    }
}