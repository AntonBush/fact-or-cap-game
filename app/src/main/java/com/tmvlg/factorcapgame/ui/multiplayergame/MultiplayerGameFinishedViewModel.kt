package com.tmvlg.factorcapgame.ui.multiplayergame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class MultiplayerGameFinishedViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var _lobbyPlayers = firebaseLobbyRepository.subscribeOnPlayersLD()
    val lobbyPlayers = _lobbyPlayers.map { it }

    fun connectToLobby(lobbyId: String) = viewModelScope.launch {
        firebaseLobbyRepository.listenLobbyPlayers(lobbyId)
    }

    fun getUsername(): String {
        return FactOrCapAuth.currentUser.value?.name
            ?: throw IllegalStateException("User is unauthorized")
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

    fun getWinner(): String {
        val winner = firebaseLobbyRepository.calculateWinner()
        winner.isWinner = true
        return winner.name
    }

    fun finish(lobbyId: String) {
        firebaseLobbyRepository.stopListeningPlayers(lobbyId)
    }
}
