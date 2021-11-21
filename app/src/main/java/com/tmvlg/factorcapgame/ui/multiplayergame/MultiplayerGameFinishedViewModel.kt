package com.tmvlg.factorcapgame.ui.multiplayergame

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Player
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class MultiplayerGameFinishedViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val userRepository: UserRepository
): ViewModel() {

    val score = MutableLiveData<Int>()

    val lobbyId = MutableLiveData<String>()

    private var _lobbyPlayers = firebaseLobbyRepository.subscribeOnPlayersLD()
    val lobbyPlayers = _lobbyPlayers.map { it }

    fun lobbies(lobbyId: String) = viewModelScope.launch {
        firebaseLobbyRepository.listenLobbyPlayers(lobbyId)
        Log.d("1", "players = ${_lobbyPlayers.value}")
    }

    fun sendScore(score: Int) {
        val username = userRepository.getUsername()
        firebaseLobbyRepository.updatePlayerScore(score, lobbyId.value!!, username)
    }

}