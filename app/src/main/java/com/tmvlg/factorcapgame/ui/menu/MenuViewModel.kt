package com.tmvlg.factorcapgame.ui.menu

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.data.preferences.PreferenceProvider
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import kotlinx.coroutines.launch

class MenuViewModel(
    private val firebaseLobbyRepository: FirebaseLobbyRepository
) : ViewModel() {
    val user = FactOrCapAuth.currentUser.map { it }
    val username = user.map { it?.name }

    val isUserSignedIn = user.map { it != null }

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage = _errorMessage.map { it }

    val authError = FactOrCapAuth.errorMessage.map { it }

    val createdLobbyId = MutableLiveData<String?>(null)

    fun signIn(
        activity: AppCompatActivity,
        launcher: FactOrCapAuth.SignInLauncher
    ) = viewModelScope.launch {
        FactOrCapAuth.signIn(activity, launcher)
    }

    fun silentSignIn(activity: AppCompatActivity, errorCallback: (() -> Unit)? = null) = viewModelScope.launch {
        FactOrCapAuth.silentSignIn(activity, errorCallback)
    }

    fun signOut(activity: AppCompatActivity) = viewModelScope.launch {
        FactOrCapAuth.signOut(activity)
    }

    fun turnVolume(turnOnVolume: Boolean, context: Context) = viewModelScope.launch {
        PreferenceProvider(context).turnVolume(turnOnVolume)
    }

    fun createLobby(roomName: String) = viewModelScope.launch {
        try {
            val roomId = firebaseLobbyRepository.createLobby(
                username.value ?: throw IllegalStateException("User is null"),
                roomName
            )
            _errorMessage.postValue(null)
            Log.d(TAG, "create lobby id: $roomId")
            createdLobbyId.postValue(roomId)
        } catch (e: IllegalStateException) {
            _errorMessage.postValue(e.message)
        }
    }

    companion object {
        private const val TAG = "MenuViewModel"
    }
}
