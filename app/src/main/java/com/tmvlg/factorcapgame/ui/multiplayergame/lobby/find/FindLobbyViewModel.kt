package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

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
    private val originLobbies = firebaseLobbyRepository
        .lobbyList.map { it.filter { lobby -> !lobby.started } }

    val dummyLobbies = originLobbies.map { it }
    private val filteredLobbies = MutableLiveData<List<Lobby>>(emptyList())
    val lobbies = filteredLobbies.map { it }

//        firebaseLobbyRepository.lobbyList.map { lobbyList ->
//        val currentMillis = System.currentTimeMillis()
//        return@map lobbyList.filter {
//            return@filter (currentMillis - it.lastTimeHostPing < LOBBY_TIMEOUT_MILLIS)
//                    && !it.started
//        }
//    }

    val connectedLobbyId = MutableLiveData<String?>(null)

    var filteringThread: FilteringThread? = null

    private fun listenLobbies() = viewModelScope.launch {
        firebaseLobbyRepository.listenLobbies()
        val thread = FilteringThread(
            originLobbies,
            filteredLobbies
        )
        thread.start()
        filteringThread = thread
    }

    fun stopListenLobbies() = viewModelScope.launch {
        val thread = filteringThread
        if (thread != null) {
            thread.interrupt()
            filteringThread = null
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
                filterDst.postValue(
                    filterLobbyList(
                        filterSrc.value ?: emptyList()
                    )
                )
                sleep(SLEEP_TIME_MILLIS)
            }
        }

        companion object {
            private fun filterLobbyList(lobbies: List<Lobby>): List<Lobby> {
                val currentMillis = System.currentTimeMillis()
                return lobbies.filter {
                    !isTimeout(it.lastTimeHostPing, currentMillis)
                }
            }

            private fun isTimeout(
                examined: Long,
                current: Long = System.currentTimeMillis()
            ): Boolean {
                return current - examined > LOBBY_TIMEOUT_MILLIS
            }

            const val SLEEP_TIME_MILLIS = 100L
            const val LOBBY_TIMEOUT_MILLIS = 10_000L
        }
    }
}
