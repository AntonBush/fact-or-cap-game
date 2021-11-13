package com.tmvlg.factorcapgame.ui.menu

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class MenuViewModel : ViewModel() {
    lateinit var auth: FirebaseAuth // Var for Firebase auth
    lateinit var googleSignInClient: GoogleSignInClient // Var for Google auth
    lateinit var menufragment: MenuFragment

// Function to auth with google account
fun googleAuth(result: ActivityResult, activity: Activity, context: Context){
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data) // Get data from intent
    try {
        // Google Sign In was successful, authenticate with Firebase
        val account = task.getResult(ApiException::class.java)!! // Get signed in account
        Log.d(GOOGLETAG, "firebaseAuthWithGoogle:" + account.id)
        firebaseAuthWithGoogle(account.idToken!!,activity,context) // Call auth with Firebase
    } catch (e: ApiException) {
        // Google Sign In failed, update UI appropriately
        Log.d(GOOGLETAG, "Google sign in failed")
        Toast.makeText(context, "Authentication via Google failed", Toast.LENGTH_SHORT).show()
    }
}
    // Function auth in Firebase with Google token
    private fun firebaseAuthWithGoogle(idToken: String, activity:Activity, context: Context) {
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
    companion object {
        private const val GOOGLETAG = "GoogleActivityViewModel"
    }
}
