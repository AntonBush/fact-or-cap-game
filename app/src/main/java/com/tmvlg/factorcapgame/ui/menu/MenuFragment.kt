package com.tmvlg.factorcapgame.ui.menu

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentMenuBinding
import com.tmvlg.factorcapgame.ui.MainActivity
import com.tmvlg.factorcapgame.ui.multiplayergame.FindLobbyFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.LobbyFragment
import com.tmvlg.factorcapgame.ui.singlegame.SingleGameFragment
import com.tmvlg.factorcapgame.ui.statisitics.StatisticsFragment

class MenuFragment : Fragment() {

    private lateinit var viewModel: MenuViewModel
    private var _binding: FragmentMenuBinding? = null
    private val binding: FragmentMenuBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        // Start single game button listener
        binding.singleGameButton.setOnClickListener() {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, SingleGameFragment())
                .commit()
        }
        // Statistics button listener
        binding.statButton.setOnClickListener() {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, StatisticsFragment())
                .commit()
        }
        // Leader button listener
        binding.leaderboardButton.setOnClickListener() {
            Toast.makeText(this.context, "In development", Toast.LENGTH_SHORT).show()
        }
        // Sign in button listener for toggling sign out button visibility
        binding.signInLayoutAuthorized.signedUserCardview.setOnClickListener {
            binding.signInLayoutAuthorized.signOutLayout.visibility =
                binding.signInLayoutAuthorized.signOutLayout.visibility.xor(8)
        }
        // Sign in button listener
        binding.signInLayoutUnauthorized.googleSignInCardview.setOnClickListener {
            // Check if user is signed in in firebase
            if (viewModel.auth.currentUser != null) {
                Log.d(GOOGLETAG, "User already signed in")
                updateUI(viewModel.auth.currentUser)
            } else {
                Log.d(GOOGLETAG, "Signing in")
                startSignInIntent() // Calling sign in intent; Start auth via Google
            }
        }
        // Sign out button listener
        binding.signInLayoutAuthorized.signOutButton.setOnClickListener {
            Log.d(GOOGLETAG, "Sign out")
            googleSignOut() // Calling sign out function
        }
        // Multiplayer game button listener
        binding.multiplayerGameButton.setOnClickListener() {
            toggleMultiplayerButton() // Calling toggle mpb function
        }
        // Create room button listener
        binding.createRoomButton.setOnClickListener() {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, LobbyFragment())
                .commit()
        }
        // Join room button listener
        binding.joinRoomButton.setOnClickListener() {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, FindLobbyFragment())
                .commit()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MenuViewModel::class.java]
        viewModel.menufragment = this // Setting viewmodel variables
        viewModel.auth = Firebase.auth // Setting viewmodel variables

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build() // Setting up google sign in parameters
        viewModel.googleSignInClient = GoogleSignIn.getClient(this.activity as Activity, gso) // Setting viewmodel variables
        updateUI(viewModel.auth.currentUser) // Update UI if user is signed in or not
    }

    // Google start auth function
    private fun startSignInIntent() {
        val signInIntent = viewModel.googleSignInClient.signInIntent // Init google sign in intent
        launcher.launch(signInIntent) // Same as startActivityForResult
    }
    // Same as onActivityResult (Listener)
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.googleAuth(result, (activity as Activity), requireContext()) // Calling viewmodel function to get data
        }
    }
    // Sign out function
    private fun googleSignOut() {
        viewModel.googleSignInClient.signOut() // Sign out from Google
        viewModel.auth.signOut() // Sign out from Firebase
        updateUI(viewModel.auth.currentUser) // UpdateUI
    }
    // Function to Update Username and sign in status on fragment
    fun updateUI(user: FirebaseUser?) {
        val username: String = user?.email ?: "" // Get user email from firebase
        (activity as MainActivity).username = username // Update username in MainActivity
        Log.d(GOOGLETAG, username + " - " + viewModel.auth.currentUser?.email.toString())

        checkUser(username.dropLast(10)) // Update xml dep on sign in status
    }
    // Function to update xml dep on sign in status
    private fun checkUser(username: String) {
        if (username.isEmpty()) {
            binding.signInLayoutUnauthorized.root.visibility = View.VISIBLE
            binding.signInLayoutAuthorized.root.visibility = View.INVISIBLE
        } else {
            binding.signInLayoutUnauthorized.root.visibility = View.INVISIBLE
            binding.signInLayoutAuthorized.root.visibility = View.VISIBLE
            binding.signInLayoutAuthorized.usernameTextview.text = getString(R.string.hello_string, username)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // Animation
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
    companion object {
        private const val GOOGLETAG = "GoogleActivity"
    }
}
