package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    val lobby = _lobby.map {
        return@map if (isLoaded.value == true) {
            it
        } else {
            null
        }
    }
    val isHost = lobby.map { it?.hostName == username }

    private val isLoaded = MutableLiveData(false)
    val isGameStarted = lobby.map {
        it?.started ?: false
    }

    val isDisconnected = lobby.map { lobby ->
        val l = lobby ?: return@map false
        l.players.none { it.name == username }
    }

    private var receivingJob: Job? = null
    private var pingJob: Job? = null
    private val pingThread = PingThread(
        firebaseLobbyRepository,
        username
    )

    // DEBUG
//    fun debugTime(tag: String, condition: () -> Boolean, delayDuration: Long) =
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                val startTime = System.nanoTime()
//                while (condition()) {
//                    val elapsedTime = (System.nanoTime() - startTime) / NANOS_IN_MILLIS
//                    Log.d("TIME ELAPSED: $elapsedTime", tag)
//                    delay(delayDuration)
//                }
//                val elapsedTime = (System.nanoTime() - startTime) / NANOS_IN_MILLIS
//                Log.d("TOTAL TIME ELAPSED: $elapsedTime", tag)
//            }
//        }

    fun listenLobby(lobbyId: String) = viewModelScope.launch {
        firebaseLobbyRepository.addPlayerToLobby(username, lobbyId)
        firebaseLobbyRepository.listenLobby(lobbyId)

        pingThread.interrupted = false
        pingJob = viewModelScope.launch {
            pingThread.start()
        }

        // DEBUG
//        debugTime("START_PING", { isLoaded.value != true }, 10)
//        debugTime("FIRST_NON_LOBBY", { firebaseLobbyRepository.lobby.get() == null }, 10)
//        debugTime("LOCAL_FIRST_NON_LOBBY", { _lobby.value == null }, 10)
//        debugTime("PING", { pingThread.lobby.get() == null }, 10)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (!pingThread.interrupted && !pingThread.isStarted.get()) {
                    delay(PingThread.SHORT_SLEEP_TIME_MILLIS)
                }
                isLoaded.postValue(true)
                Log.d(TAG, "Ping loaded")
            }
        }
        Log.d("LobbyViewModel", "Before receive job launched")
        receivingJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    val receivedLobby = firebaseLobbyRepository.lobby.get()

                    if (receivedLobby != null) {
                        _lobby.postValue(receivedLobby)
                        pingThread.lobby.set(receivedLobby)
                    }

                    if (!pingThread.isStarted.get()) {
                        delay(SHORT_SLEEP_TIME_MILLIS)
                    } else {
                        delay(SLEEP_TIME_MILLIS)
                    }
                }
            }
        }
        Log.d("LobbyViewModel", "Listen lobby end")
    }

    fun stopListenLobby() = viewModelScope.launch {
        val ping = pingJob
        if (ping != null) {
            pingThread.interrupt()
            ping.join()
        }
        val job = receivingJob
        if (job != null) {
            job.cancel()
            receivingJob = null
        }
        isLoaded.postValue(false)
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

        if (isHost.value == false ||
            lobby == null ||
            lobby.hostName == lobby.players.find { it.id == userId }?.name
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
        const val NANOS_IN_MILLIS = 1_000_000L
        const val SLEEP_TIME_MILLIS = 300L
        const val SHORT_SLEEP_TIME_MILLIS = 30L
    }
}
