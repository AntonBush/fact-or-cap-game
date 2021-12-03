package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import android.util.Log
import androidx.lifecycle.*
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

class FindLobbyViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _firebaseLobbies = firebaseLobbyRepository.lobbyList.map { lobbyList ->
        val l = lobbyList.filter {
            !it.started
        }
        Log.d("11111111111", "${l.size}")
        l
    }
    val firebaseLobbies = _firebaseLobbies.map {
        Log.d("222222222222", "${it.size}")
        it
    }

    private val _lobbies = MutableLiveData<List<Lobby>>(emptyList())
    val lobbies = _lobbies.map { it }


//        firebaseLobbyRepository.lobbyList.map { lobbyList ->
//        val currentMillis = System.currentTimeMillis()
//        return@map lobbyList.filter {
//            return@filter (currentMillis - it.lastTimeHostPing < LOBBY_TIMEOUT_MILLIS)
//                    && !it.started
//        }
//    }

    val connectedLobbyId = MutableLiveData<String?>(null)

    var filterThread: Thread? = null

    private fun listenLobbies() = viewModelScope.launch {
        firebaseLobbyRepository.listenLobbies()
        val thread = FilteringThread(
            firebaseLobbies,
            _lobbies
        )
        thread.start()
        filterThread = thread
    }

    fun stopListenLobbies() = viewModelScope.launch {
        firebaseLobbyRepository.stopListenLobbies()
        val thread = filterThread
        if (thread != null) {
            thread.interrupt()
            filterThread = null
        }
    }

    fun connectLobby(selectedLobby: Lobby) = viewModelScope.launch {
        firebaseLobbyRepository.addPlayerToLobby(
            FactOrCapAuth.currentUser.value?.name
                ?: throw IllegalStateException("User is unauthorized"),
            selectedLobby.id
        )
        connectedLobbyId.postValue(selectedLobby.id)
    }

    init {
        listenLobbies()
    }

    class FilteringThread(
        private val filterSrc: LiveData<List<Lobby>>,
        private val filterDst: MutableLiveData<List<Lobby>>
    ) : Thread() {
        private var interrupted = false

        override fun run() {
            while (!interrupted) {
                val currentMillis = System.currentTimeMillis()
                Log.d(
                    "THREADD",
                    "origin ${ filterSrc.value?.size?.toString() ?: "null" }"
                )
                val filtered = filterSrc.value?.filter {
                    val dif = currentMillis - it.lastTimeHostPing
                    Log.d("THREADD", "dif $dif")
                    dif < LOBBY_TIMEOUT_MILLIS
                } ?: emptyList()
                Log.d(
                    "THREADD",
                    " filtered ${filtered.size}"
                )
                filterDst.postValue(filtered)
                sleep(SLEEP_TIME_MILLIS)
            }
        }

        override fun interrupt() {
            interrupted = true
        }

        companion object {
            const val SLEEP_TIME_MILLIS = 100L
            const val LOBBY_TIMEOUT_MILLIS = 5_000L
        }
    }
}
