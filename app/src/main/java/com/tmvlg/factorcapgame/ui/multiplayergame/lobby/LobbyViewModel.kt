package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.util.Log
import androidx.lifecycle.*
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

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

    private val _lobby = MutableLiveData<Lobby>(null)
    val lobby = _lobby.map { it }
    val isHost = lobby.map { it?.hostName == username }

    val isGameStarted = lobby.map {
        it?.started ?: false
    }

    val isDisconnected = lobby.map { lobby ->
        val l = lobby ?: return@map false
        l.players.none { it.name == username }
    }

    private var receivingJob: Job? = null
    private var pingThread: PingThread? = null

    fun listenLobby(lobbyId: String) = viewModelScope.launch {
        firebaseLobbyRepository.addPlayerToLobby(username, lobbyId)
        firebaseLobbyRepository.listenLobby(lobbyId)

        Log.d("LobbyViewModel", "Before receive job launched")

        val lobbyChannel = Channel<Lobby>()
        receivingJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    val l = firebaseLobbyRepository.lobby.get()
                    Log.d(TAG, "lobby received ${l?.id}")

                    if (l != null) {
                        _lobby.postValue(l)
                        lobbyChannel.send(l)
                    }

                    delay(PingThread.SLEEP_TIME_MILLIS)
                }
            }
        }

        Log.d("LobbyViewModel", "After receive job launched")
        val thread = PingThread(
            firebaseLobbyRepository,
            username,
            lobbyChannel
        )
        thread.start()
        pingThread = thread
        Log.d("LobbyViewModel", "Listen lobby end")
    }

    fun stopListenLobby() = viewModelScope.launch {
        val thread = pingThread
        if (thread != null) {
            thread.interrupt()
            pingThread = null
        }
        val job = receivingJob
        if (job != null) {
            job.cancel()
            receivingJob = null
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
