package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.util.Log
import androidx.lifecycle.*
import com.tmvlg.factorcapgame.data.FactOrCapAuth
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
            _username = FactOrCapAuth.currentUser.value?.name
                ?: throw IllegalStateException("User is unauthorized")
        }
    }

    val lobby = firebaseLobbyRepository.lobby.map { it }
    val isHost = lobby.map { it?.hostName == username }

    val isGameStarted = lobby.map { it?.started ?: false }

    val isDisconnected = lobby.map { lobby ->
        val l = lobby ?: return@map false

        Log.d("LobbyViewModel", "username: $username")
        Log.d("LobbyViewModel", "players: ${l.players}")
        l.players.none { it.name == username }
        false
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

    fun removePlayer(userName: String) {
        val lobby = lobby.value
        if (isHost.value == true && lobby != null && lobby.hostName != userName) {
            val userIdToRemove = lobby.players.find { it.name == userName }?.id ?: return
            firebaseLobbyRepository.removePlayer(
                lobby.id,
                userIdToRemove
            )
        }
    }

    class PingThread(
        private val firebaseLobbyRepository: FirebaseLobbyRepository,
        private val username: String,
        private val lobby: LiveData<Lobby?>
    ) : SoftInterruptThread() {
        override fun run() {
            while (!interrupted) {
                val lobby = lobby.value
                val player = lobby?.players?.find { it.name == username }

                Log.d("THREAD LOBBY", "player is ${player?.name ?: "null"}")
                if (lobby != null && player != null) {
                    Log.d("THREAD LOBBY", "player is ${player.name}")

                    firebaseLobbyRepository.updatePing(lobby.id, player.id)

                    if (lobby.hostName == player.name) {
                        Log.d("THREAD LOBBY", "player is host")
                        firebaseLobbyRepository.updateLobbyPing(lobby.id)
                    }
                }
                sleep(SLEEP_TIME_MILLIS)
            }
        }

        companion object {
            const val SLEEP_TIME_MILLIS = 1_000L
        }
    }
}
