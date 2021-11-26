package com.tmvlg.factorcapgame.ui.multiplayergame

import android.util.Log
import androidx.lifecycle.*
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class MultiplayerGameFinishedViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private var _lobbyPlayers = firebaseLobbyRepository.subscribeOnPlayersLD()
    val lobbyPlayers = _lobbyPlayers.map { it }

    fun connectToLobby(lobbyId: String) = viewModelScope.launch {
        firebaseLobbyRepository.listenLobbyPlayers(lobbyId)
    }

    fun getUsername(): String {
        return userRepository.getUsername()
    }

    fun sendScore(score: Int, lobbyId: String) {
        val username = getUsername()
        firebaseLobbyRepository.updatePlayerScore(score, lobbyId, username)
    }

    fun isAllFinished(): Boolean {
        lobbyPlayers.value?.forEach {
            if (!it.waiting)
                return false
        }
        return true
    }

    fun getWinner(lobbyId: String): String {
        val winner = firebaseLobbyRepository.calculateWinner()
        winner.isWinner = true
        return winner.playerName
    }

    fun finish(lobbyId: String) {
        firebaseLobbyRepository.stopListeningPlayers(lobbyId)
    }

}