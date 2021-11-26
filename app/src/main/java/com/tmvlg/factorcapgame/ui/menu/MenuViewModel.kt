package com.tmvlg.factorcapgame.ui.menu

import android.app.Activity
import android.content.Context
import android.service.controls.Control
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class MenuViewModel(
    private val userRepository: UserRepository,

) : ViewModel() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private val user = MutableLiveData<FirebaseUser?>()
    val username = user.map { it?.email?.dropLast(EMAIL_LETTERS_COUNT) }
    val isUserSignedIn = user.map { it != null }

    fun initializeAuth(activity: Activity) = viewModelScope.launch {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun signIn() = viewModelScope.launch {

    }

    fun signOut() = viewModelScope.launch {

    }

    // Function to auth with google account
    fun googleAuth(result: ActivityResult, activity: Activity, context: Context) = viewModelScope.launch {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data) // Get data from intent
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!! // Get signed in account
            Log.d(GOOGLETAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!, activity, context) // Call auth with Firebase
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.d(GOOGLETAG, "Google sign in failed")
            Toast.makeText(context, "Authentication via Google failed", Toast.LENGTH_SHORT).show()
            menufragment.updateUI(null)
        }
    }
    // Function auth in Firebase with Google token
    private fun firebaseAuthWithGoogle(idToken: String, activity: Activity, context: Context) = viewModelScope.launch {
        val credential = GoogleAuthProvider.getCredential(idToken, null) // Get user data from firebase
        auth.signInWithCredential(credential) // Sign in with firebase user data
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(GOOGLETAG, "signInWithCredential:success")
                    menufragment.updateUI(auth.currentUser) // UpdateUI with user
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(GOOGLETAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Authentication via Firebase failed", Toast.LENGTH_SHORT).show()
                    menufragment.updateUI(null) // UpdateUI with no user
                }
            }
    }

    fun saveUser(username: String?) = viewModelScope.launch {
        userRepository.saveUsername(username)
    }

    companion object {
        const val EMAIL_LETTERS_COUNT = "@gmail.com".length
        private const val TAG = "MenuViewModel"
    }
}
