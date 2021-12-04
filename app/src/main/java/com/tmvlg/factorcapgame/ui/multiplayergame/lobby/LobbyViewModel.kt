package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.data.repository.firebase.Player
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
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

    val lobby = firebaseLobbyRepository.lobby.map {
       Log.d("LobbyViewModel.lobby", "${it}")
        it
    }
    val isHost = lobby.map { it?.hostName == username }

    val isGameStarted = lobby.map {
        Log.d("LobbyViewModel", "------------ $it")
        Log.d("LobbyViewModel", "------------ ${it?.started ?: false}")
        it?.started ?: false }

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
        Log.d("LobbyViewModel", "+++++ $lobbyId")
        if (lobbyId != null) {
            Log.d("LobbyViewModel", "+++++ $lobbyId")
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
        private val lobbyLiveData: LiveData<Lobby?>
    ) : SoftInterruptThread() {
        override fun run() {
            while (!interrupted) {
                val lobby = lobbyLiveData.value
                val player = lobby?.players?.find { it.name == username }

                if (lobby == null || player == null) {
                    continue
                }

                firebaseLobbyRepository.updatePing(lobby.id, player.id)

                doHostStuff(lobby, player)

                sleep(SLEEP_TIME_MILLIS)
            }
        }

        private fun doHostStuff(lobby: Lobby, player: Player) {
            if (lobby.hostName != player.name) {
                return
            }

            firebaseLobbyRepository.updateLobbyPing(lobby.id)

            val currentMillis = System.currentTimeMillis()

            lobby.players.forEach { p ->
                if (p.name == lobby.hostName) {
                    return@forEach
                }

                val diff = currentMillis - p.lastTimePing
                if (diff > PLAYER_TIMEOUT_MILLIS) {
                    firebaseLobbyRepository.removePlayer(lobby.id, p.id)
                }
            }
        }

        companion object {
            const val SLEEP_TIME_MILLIS = 1_000L
            const val PLAYER_TIMEOUT_MILLIS = 10_000L
        }
    }
}
