package com.tmvlg.factorcapgame.ui.multiplayergame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseGameRepository
import kotlinx.coroutines.launch

class MultiplayerGameFinishedViewModel(
    private val firebaseGameRepository: FirebaseGameRepository
) : ViewModel() {

    private var _lobbyPlayers = firebaseGameRepository.subscribeOnPlayersLD()
    val lobbyPlayers = _lobbyPlayers.map { it }

    fun connectToLobby(lobbyId: String) = viewModelScope.launch {
        firebaseGameRepository.listenLobbyPlayers(lobbyId)
    }

    fun getUsername(): String {
        return FactOrCapAuth.currentUser.value?.name
            ?: throw IllegalStateException("User is unauthorized")
    }

    fun sendScore(score: Int, lobbyId: String) {
        val username = getUsername()
        firebaseGameRepository.updatePlayerScore(score, lobbyId, username)
    }

    fun isAllFinished(): Boolean {
        lobbyPlayers.value?.forEach {
            if (!it.waiting)
                return false
        }
        return true
    }

    fun getWinner(): String {
        val winner = firebaseGameRepository.calculateWinner()
        winner.isWinner = true
        return winner.name
    }

    fun finish(lobbyId: String) {
        firebaseGameRepository.stopListeningPlayers(lobbyId)
    }
}
