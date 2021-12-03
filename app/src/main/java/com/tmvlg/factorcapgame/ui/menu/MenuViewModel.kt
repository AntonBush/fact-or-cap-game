package com.tmvlg.factorcapgame.ui.menu

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.data.preferences.PreferenceProvider
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
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

    fun silentSignIn(activity: AppCompatActivity) = viewModelScope.launch {
        FactOrCapAuth.silentSignIn(activity)
    }

    fun signOut(activity: AppCompatActivity) = viewModelScope.launch {
        FactOrCapAuth.signOut(activity)
    }

    fun createLobby(roomName: String) = viewModelScope.launch {
        try {
            val roomId = firebaseLobbyRepository.createLobby(
                username.value ?: throw IllegalStateException("User is null"),
                roomName
            )
            _errorMessage.postValue(null)
            createdLobbyId.postValue(roomId)
        } catch (e: Exception) {
            _errorMessage.postValue(e.message)
        }
    }

    companion object {
        private const val TAG = "MenuViewModel"
    }
}
