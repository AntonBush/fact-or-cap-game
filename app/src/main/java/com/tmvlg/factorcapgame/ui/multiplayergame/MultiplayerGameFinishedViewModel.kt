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

    private var _lobbyPlayers = firebaseLobbyRepository.subscribeOnPlayersLD()
    val lobbyPlayers = _lobbyPlayers.map { it }

    fun lobbies(lobbyId: String) = viewModelScope.launch {
        firebaseLobbyRepository.listenLobbyPlayers(lobbyId)
        Log.d("1", "players = ${_lobbyPlayers.value}")
    }

    fun sendScore(score: Int, lobbyId: String) {
        val username = userRepository.getUsername()
        firebaseLobbyRepository.updatePlayerScore(score, lobbyId, username)
    }

    fun isAllFinished(): Boolean {
        lobbyPlayers.value?.forEach {
            if (!it.isFinished)
                return false
        }
        return true
    }

    fun getWinner(lobbyId: String): Player {
        return firebaseLobbyRepository.calculateWinner(lobbyId)
    }

}