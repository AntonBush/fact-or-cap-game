package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.util.Log
import androidx.lifecycle.*
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.data.repository.firebase.Player
import kotlinx.coroutines.launch

class LobbyViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository
) : ViewModel() {
    private var _username: String? = null
    val username: String
        get() = _username ?: throw IllegalStateException("Username should be loaded")

    init {
        viewModelScope.launch {
            _username = FactOrCapAuth.currentUser.value?.name
                ?: throw IllegalStateException("User is unauthorized")
        }
    }

    val lobby = firebaseLobbyRepository.lobby.map { it }
    val isHost = lobby.map { it?.hostName == username }

    val isGameStarted = lobby.map {
        it?.started ?: false
    }

    val isDisconnected = lobby.map { lobby ->
        val l = lobby ?: return@map false
        l.players.none { it.name == username }
    }

    var pingThread: PingThread? = null

    fun listenLobby(lobbyId: String) = viewModelScope.launch {
        firebaseLobbyRepository.addPlayerToLobby(username, lobbyId)
        firebaseLobbyRepository.listenLobby(lobbyId)
        val thread = PingThread(
            firebaseLobbyRepository,
            username,
            lobby
        )
        thread.start()
        pingThread = thread
    }

    fun stopListenLobby() = viewModelScope.launch {
        val thread = pingThread
        if (thread != null) {
            thread.interrupt()
            pingThread = null
        }
        firebaseLobbyRepository.stopListenLobby()
    }

    fun startGame() = viewModelScope.launch {
        val lobbyId = lobby.value?.id
        if (lobbyId != null) {
            firebaseLobbyRepository.startGame(lobbyId)
        }
    }

    fun removePlayer(userId: String) = viewModelScope.launch {
        val lobby = lobby.value

        if (isHost.value == false
            || lobby == null
            || lobby.hostName == lobby.players.find { it.id == userId }?.name
        ) {
            return@launch
        }

        firebaseLobbyRepository.removePlayer(
            lobby.id,
            userId
        )
    }

    companion object {
        const val TAG = "LobbyViewModel"
    }
}
