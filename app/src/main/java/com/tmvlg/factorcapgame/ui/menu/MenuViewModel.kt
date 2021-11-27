package com.tmvlg.factorcapgame.ui.menu

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
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
    private val firebaseAuth by lazy { Firebase.auth }

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
        googleSignInClient.silentSignIn().addOnSuccessListener { account ->
            firebaseSignIn(account.idToken!!, activity)
        }
    }

    fun startSignInIntent(launcher: ActivityResultLauncher<Intent>) = viewModelScope.launch {
        _errorMessage.postValue(null)
        // Same as startActivityForResult
        launcher.launch(googleSignInClient.signInIntent)
    }

    fun signInFromIntent(result: ActivityResult, activity: Activity) = viewModelScope.launch {
        if (result.resultCode != Activity.RESULT_OK) {
            _errorMessage.postValue("Intent failure")
            _user.postValue(null)
            return@launch
        }
        // Calling viewModel function to get data
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data) // Get data from intent
        task.addOnCompleteListener(activity) { resultTask ->
            if (!resultTask.isSuccessful) {
                Log.w(TAG, "googleSignIn failure")
                _errorMessage.postValue("Authentication via Google failed")
                _user.postValue(null)
                return@addOnCompleteListener
            }

            val idToken = resultTask.result.idToken!!
            Log.i(TAG, "googleSignIn success: <$idToken>")
            firebaseSignIn(idToken, activity)
        }
    }

    fun signOut() = viewModelScope.launch {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
        saveUser().join()
        _user.postValue(null)
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

    private fun saveUser() = viewModelScope.launch {
        userRepository.saveUsername(
            firebaseAuth.currentUser?.email?.dropLast(EMAIL_LETTERS_COUNT)
        )
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
