package com.tmvlg.factorcapgame.ui.menu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentMenuBinding
import com.tmvlg.factorcapgame.ui.singlegame.SingleGameFragment
import com.tmvlg.factorcapgame.ui.statisitics.StatisticsFragment

class MenuFragment : Fragment() {

    private lateinit var viewModel: MenuViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var _binding: FragmentMenuBinding? = null
    private val binding: FragmentMenuBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        val username = arguments?.getString("Username").toString()

        checkuser(username)

        val singleGameFragmentWU = SingleGameFragment()
        singleGameFragmentWU.arguments = Bundle().apply {
            putString("Username", username)
        }
        val statisticsFragmentWU = StatisticsFragment()
        statisticsFragmentWU.arguments = Bundle().apply {
            putString("Username", username)
        }

        binding.signInLayoutAuthorized.signedUserCardview.setOnClickListener {
            binding.signInLayoutAuthorized.signOutLayout.visibility =
                binding.signInLayoutAuthorized.signOutLayout.visibility.xor(8)
        }
        binding.signInLayoutAuthorized.signOutButton.setOnClickListener {
            Log.d("UpdateUI", "sign out")
            auth.signOut()
            updateUI(auth.currentUser)
        }
        binding.singleGameButton.setOnClickListener() {
            val singleGameFragmentWU = SingleGameFragment()
            singleGameFragmentWU.arguments = Bundle().apply {
                putString("Username", username)
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, singleGameFragmentWU)
                .commit()
        }
        binding.statButton.setOnClickListener() {
            val statisticsFragmentWU = StatisticsFragment()
            statisticsFragmentWU.arguments = Bundle().apply {
                putString("Username", username)
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, statisticsFragmentWU)
                .commit()
        }
        binding.leaderboardButton.setOnClickListener() {
            Toast.makeText(this.context, "In development", Toast.LENGTH_SHORT).show()
        }
        binding.signInLayoutUnauthorized.googleSignInCardview.setOnClickListener {
            //TODO("Реализация входа в аккаунт")
            //Toast.makeText(this.context, "In development", Toast.LENGTH_SHORT).show()
            if (auth.currentUser != null) {
                Log.d(TAG, "User already logged in")
                updateUI(auth.currentUser)
            } else {
                Log.d(TAG, "Signing in")
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                googleSignInClient = GoogleSignIn.getClient(this.activity as Activity, gso)
                startSignInIntent()
            }
            Log.d(TAG, "PRESSED")
        }
        binding.createRoomButton.setOnClickListener() {
            Toast.makeText(this.context, "In development", Toast.LENGTH_SHORT).show()
        }
        binding.joinRoomButton.setOnClickListener() {
            Toast.makeText(this.context, "In development", Toast.LENGTH_SHORT).show()
        }
        binding.multiplayerGameButton.setOnClickListener() {
            toggleMultiplayerButton()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MenuViewModel::class.java]
        if (auth.currentUser != null) {
            Log.d(TAG, "User already logged in")
            updateUI(auth.currentUser)
        }
    }
    // Надо переделать под новый стиль
    private fun startSignInIntent() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun checkuser(username:String){
        if (username == "") {
            binding.signInLayoutUnauthorized.root.visibility = View.VISIBLE
            binding.signInLayoutAuthorized.root.visibility = View.INVISIBLE
        } else {
            binding.signInLayoutUnauthorized.root.visibility = View.INVISIBLE
            binding.signInLayoutAuthorized.root.visibility = View.VISIBLE
            binding.signInLayoutAuthorized.usernameTextview.text = "Hello, $username"
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "Google sign in failed")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this.activity as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        val username: String = user?.email ?: ""
        Log.d("UpdateUI", username +"         " + auth.currentUser?.email.toString())
        checkuser(username)
        //TODO(ОБЕРНУТЬ В ФУНКЦИЮ)
        binding.signInLayoutAuthorized.usernameTextview.text = username.dropLast(10)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

// Этот код можно будет использовать позже в сетевой игре
//
//    private fun getUserInfo() {
//        val user = auth.currentUser
//        user?.let {
//            val playerName = user.displayName
//            val email = user.email
//            // The user's Id, unique to the Firebase project.
//            // Do NOT use this value to authenticate with your backend server, if you
//            // have one; use FirebaseUser.getIdToken() instead.
//            val uid = user.uid
//        }
//    }
    private fun toggleMultiplayerButton() {
        val visibility = binding.joinRoomButton.visibility.xor(4)
        val upperAnim = if (visibility == View.VISIBLE) {
            AnimationUtils.loadAnimation(requireContext(), R.anim.multiplayer_in_upper)
        } else {
            AnimationUtils.loadAnimation(requireContext(), R.anim.multiplayer_out_upper)
        }
        val lowerAnim = if (visibility == View.VISIBLE) {
            AnimationUtils.loadAnimation(requireContext(), R.anim.multiplayer_in_lower)
        } else {
            AnimationUtils.loadAnimation(requireContext(), R.anim.multiplayer_out_lower)
        }
        binding.createRoomButton.visibility = visibility
        binding.createRoomButton.startAnimation(upperAnim)
        binding.joinRoomButton.visibility = visibility
        binding.joinRoomButton.startAnimation(lowerAnim)
    }
}
