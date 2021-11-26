package com.tmvlg.factorcapgame.ui.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class MenuViewModel(
    private val userRepository: UserRepository,
    private val firebaseLobbyRepository: FirebaseLobbyRepository
) : ViewModel() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val firebaseAuth = Firebase.auth

    private val _user = MutableLiveData<FirebaseUser?>(null)
    val user = _user.map { it }
    val username = user.map { it?.email?.dropLast(EMAIL_LETTERS_COUNT) }
    val isUserSignedIn = user.map { it != null }

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage = _errorMessage.map { it }

    val createdLobbyId = MutableLiveData<String?>(null)

    fun initializeAuth(activity: Activity) = viewModelScope.launch {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun startSignInIntent(launcher: ActivityResultLauncher<Intent>) = viewModelScope.launch {
        _errorMessage.postValue(null)
        // Same as startActivityForResult
        launcher.launch(googleSignInClient.signInIntent)
    }

    fun signIn(result: ActivityResult, activity: Activity) = viewModelScope.launch {
        if (result.resultCode == Activity.RESULT_OK) {
            // Calling viewModel function to get data
            googleSignIn(result, activity)
        } else {
            _errorMessage.postValue("Intent failure")
            _user.postValue(null)
        }
    }

    fun signOut() = viewModelScope.launch {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
        saveUser().join()
        _user.postValue(null)
    }

    fun createLobby() = viewModelScope.launch {
        _errorMessage.postValue("Under development")
    }

    private fun saveUser() = viewModelScope.launch {
        userRepository.saveUsername(
            firebaseAuth.currentUser?.email?.dropLast(EMAIL_LETTERS_COUNT)
        )
    }

    private fun googleSignIn(
        result: ActivityResult,
        activity: Activity
    ) = viewModelScope.launch {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data) // Get data from intent
        task.addOnCompleteListener(activity) { resultTask ->
            val idToken = resultTask.result.idToken
            if (resultTask.isSuccessful && idToken != null) {
                Log.i(TAG, "googleSignIn success: <${idToken}>")
                firebaseSignIn(idToken, activity)
            } else {
                Log.w(TAG, "googleSignIn failure")
                _errorMessage.postValue("Authentication via Google failed")
                _user.postValue(null)
            }
        }
    }

    private fun firebaseSignIn(
        idToken: String,
        activity: Activity
    ) = viewModelScope.launch {
        val credential = GoogleAuthProvider
            .getCredential(idToken, null) // Get user data from firebase
        firebaseAuth.signInWithCredential(credential) // Sign in with firebase user data
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        saveUser().join()
                        _user.postValue(firebaseAuth.currentUser)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    _errorMessage.postValue("Authentication via Firebase failed")
                    _user.postValue(null)
                    signOut()
                }
            }
    }

    companion object {
        const val EMAIL_LETTERS_COUNT = "@gmail.com".length
        private const val TAG = "MenuViewModel"
    }
}
