package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.SoftInterruptThread
import kotlinx.coroutines.launch

class FindLobbyViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository
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
        val thread = filterThread
        if (thread != null) {
            thread.interrupt()
            filterThread = null
        }
        firebaseLobbyRepository.stopListenLobbies()
    }

    fun connectLobby(selectedLobby: Lobby) = viewModelScope.launch {
        connectedLobbyId.postValue(selectedLobby.id)
    }

    init {
        listenLobbies()
    }

    class FilteringThread(
        private val filterSrc: LiveData<List<Lobby>>,
        private val filterDst: MutableLiveData<List<Lobby>>
    ) : SoftInterruptThread() {
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

        companion object {
            const val SLEEP_TIME_MILLIS = 100L
            const val LOBBY_TIMEOUT_MILLIS = 10_000L
        }
    }
}
