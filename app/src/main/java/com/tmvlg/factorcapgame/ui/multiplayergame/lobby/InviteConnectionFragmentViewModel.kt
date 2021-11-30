package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class InviteConnectionFragmentViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private var _readyToConnect = MutableLiveData<Boolean>()
    val readyToConnect = _readyToConnect.map { it }


    private fun connectToLobby(lobbyId: String) = viewModelScope.launch {
        val username = userRepository.getUsername()
            ?: throw IllegalStateException("user must be initialized")
        firebaseLobbyRepository.addPlayerToLobby(username, lobbyId)
    }

    fun setup(lobbyId: String) = viewModelScope.launch {
        connectToLobby(lobbyId).join()
        _readyToConnect.postValue(true)
    }

}