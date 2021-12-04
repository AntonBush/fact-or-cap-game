package com.tmvlg.factorcapgame.data

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.data.auth.User
import com.tmvlg.factorcapgame.data.preferences.PreferenceProvider

object FactOrCapAuth {
    private const val EMAIL_LETTERS_COUNT = "@gmail.com".length
    private const val TAG = "FactOrCapAuth"

    private var _gso: GoogleSignInOptions? = null
    private fun getGso(activity: AppCompatActivity): GoogleSignInOptions {
        val localGso = _gso ?: GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        _gso = localGso
        return localGso
    }

    private val firebaseAuth = Firebase.auth

    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser = _currentUser.map { it }

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage = _errorMessage.map { it }

    fun signIn(activity: AppCompatActivity, launcher: SignInLauncher? = null, errorCallback: (() -> Unit)? = null) {
        Log.d(TAG, "signIn: before silent signIn")
        val localGoogleSignInClient = GoogleSignIn.getClient(
            activity.applicationContext,
            getGso(activity)
        )
        val signInTask = localGoogleSignInClient.silentSignIn()
            .addOnSuccessListener { account ->
                Log.d(TAG, "Successful google silent signIn")
                firebaseSignIn(account, activity)
            }
        if (launcher != null) {
            signInTask.addOnFailureListener {
                Log.d(TAG, "Starting google signIn intent...")
                launcher.launch(localGoogleSignInClient.signInIntent)
            }
        }
        if (errorCallback != null) {
            signInTask.addOnFailureListener {
                errorCallback()
            }.addOnCanceledListener {
                errorCallback()
            }
        }
    }

    fun silentSignIn(activity: AppCompatActivity, errorCallback: (() -> Unit)? = null) {
        signIn(activity, errorCallback = errorCallback)
    }

    fun signOut(activity: AppCompatActivity) {
        firestoreSignOut()

        firebaseAuth.signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(activity.applicationContext, gso)
            .signOut()

        _currentUser.postValue(null)
    }

    fun googleSignInFromIntent(intent: Intent?, activity: AppCompatActivity) {
        // Get data from intent
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        task
            .addOnSuccessListener { account -> firebaseSignIn(account, activity) }
            .addOnFailureListener {
                _errorMessage.postValue("Authentication via Google failed")
                _currentUser.postValue(null)
                signOut(activity)
            }
    }

    private fun firebaseSignIn(account: GoogleSignInAccount, activity: AppCompatActivity) {
        val username = account.email?.dropLast(EMAIL_LETTERS_COUNT)
            ?: throw IllegalStateException(
                "SignIn intent returned account with null email"
            )
        val idToken = account.idToken ?: throw IllegalStateException(
            "SignIn intent returned account with null idToken"
        )
        val user = User(username, idToken)
        // Get user data from firebase
        val credential = GoogleAuthProvider.getCredential(user.idToken, null)
        // Sign in with firebase user data
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                firestoreSignIn(
                    User(
                        username,
                        PreferenceProvider(activity.applicationContext)
                            .getRegistrationToken() ?: ""
                    )
                )
                _currentUser.postValue(user)
            }
            .addOnFailureListener {
                _errorMessage.postValue("Authentication via Firebase failed")
                _currentUser.postValue(null)
                signOut(activity)
            }
    }

    private fun firestoreSignIn(user: User) {
        Firebase.firestore.collection("users")
            .document(user.name)
            .set(user.toMap(), SetOptions.merge())
    }

    private fun firestoreSignOut() {
        val user = currentUser.value ?: return

        val mappedUser = hashMapOf(
            "username" to user.name,
            "token" to ""
        )

        Firebase.firestore.collection("users")
            .document(user.name)
            .set(mappedUser, SetOptions.merge())
    }

    class SignInLauncher private constructor(
        private val launcher: ActivityResultLauncher<Intent>
    ) : ActivityResultLauncher<Intent>() {
        override fun launch(input: Intent?, options: ActivityOptionsCompat?) {
            launcher.launch(input, options)
        }

        override fun unregister() {
            launcher.unregister()
        }

        override fun getContract(): ActivityResultContract<Intent, *> {
            return launcher.contract
        }

        companion object {
            fun newInstance(activity: AppCompatActivity): SignInLauncher {
                val launcher = activity.registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode != Activity.RESULT_OK) {
                        Log.w(TAG, "Google signIn intent failure")
                        _errorMessage.postValue("Intent failure")
                        _currentUser.postValue(null)
                        return@registerForActivityResult
                    }
                    Log.d(TAG, "Successful google signIn intent")
                    googleSignInFromIntent(result.data, activity)
                }

                return SignInLauncher(launcher)
            }
        }
    }
}
