package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.firebase.Player
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.IllegalStateException

class InviteConnectionFragmentViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private var _readyToConnect = MutableLiveData<Boolean>()
    val readyToConnect = _readyToConnect.map { it }

    private var _exception = MutableLiveData<IOException>()
    val exception = _exception.map { it }

    private fun connectToLobby(lobbyId: String) = viewModelScope.launch {
        try {
            val username = FactOrCapAuth.currentUser.value?.name
                ?: throw IllegalStateException("User is unauthorized")
            firebaseLobbyRepository.addPlayerToLobby(username, lobbyId)
        } catch (e: IOException) {
            _exception.postValue(e)
        }
    }

    fun setup(lobbyId: String) = viewModelScope.launch {
        connectToLobby(lobbyId).join()
        _readyToConnect.postValue(true)
    }

}