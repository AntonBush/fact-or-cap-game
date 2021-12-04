package com.tmvlg.factorcapgame.ui.menu

import android.R.attr
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentMenuBinding
import com.tmvlg.factorcapgame.ui.MainActivity
import com.tmvlg.factorcapgame.ui.leaderboard.LeaderboardFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.FindLobbyFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.LobbyFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.MultiplayerGameFinishedViewModel
import com.tmvlg.factorcapgame.ui.multiplayergame.MultiplayerGameFinishedViewModelFactory
import com.tmvlg.factorcapgame.ui.singlegame.SingleGameFragment
import com.tmvlg.factorcapgame.ui.statisitics.StatisticsFragment
import kotlin.random.Random
import android.animation.ValueAnimator

import android.R.attr.animationDuration
import android.animation.ValueAnimator.*
import android.view.ViewPropertyAnimator
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.interpolator.view.animation.FastOutLinearInInterpolator


class MenuFragment : Fragment() {

    private val viewModel: MenuViewModel by viewModels {
        // inits viewmodel
        val app = activity?.application as FactOrCapApplication
        return@viewModels MenuViewModelFactory(
            app.userRepository
        )
    }
    private var _binding: FragmentMenuBinding? = null
    private val binding: FragmentMenuBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        binding.pageContainer.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_change))

        if (!(this.activity as MainActivity).soundEnabled) binding.changeVolumeButton.setImageResource(R.drawable.ic_volume_off)

        // Start single game button listener
        binding.singleGameButton.setOnClickListener() {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, SingleGameFragment())
                .commit()
        }

        // Statistics button listener
        binding.statButton.setOnClickListener() {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, StatisticsFragment())
                .addToBackStack(null)
                .commit()
        }
        // Leader button listener
        binding.leaderboardButton.setOnClickListener() {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, LeaderboardFragment())
                .addToBackStack(null)
                .commit()
//            Toast.makeText(this.context, "In development", Toast.LENGTH_SHORT).show()
        }
        // Sign in button listener for toggling sign out button visibility
        binding.signInLayoutAuthorized.signedUserCardview.setOnClickListener {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            binding.signInLayoutAuthorized.signOutLayout.visibility =
                binding.signInLayoutAuthorized.signOutLayout.visibility.xor(XOR_VISIBLE_VALUE_1)
        }
        // Sign in button listener
        binding.signInLayoutUnauthorized.googleSignInCardview.setOnClickListener {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            // Check if user is signed in in firebase
            if (viewModel.auth.currentUser != null) {
                Log.d(GOOGLETAG, "User already signed in")
                updateUI(viewModel.auth.currentUser)
            } else {
                Log.d(GOOGLETAG, "Signing in")
                // Calling sign in intent; Start auth via Google
                startSignInIntent()
            }
        }
        // Sign out button listener
        binding.signInLayoutAuthorized.signOutButton.setOnClickListener {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            Log.d(GOOGLETAG, "Sign out")
            // Calling sign out function
            googleSignOut()
        }
        // Multiplayer game button listener
        binding.multiplayerGameButton.setOnClickListener() {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            // Calling toggle mpb function
            if (viewModel.auth.currentUser != null) {
                toggleMultiplayerButton()
            } else {
                MaterialAlertDialogBuilder(requireContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog)
                    .setTitle("Auth required")
                    .setMessage("You are not authorized yet." +
                            " You should authorize via Google to play multiplayer." +
                            " Do you want to proceed?")
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.cancel()
                    }
                    .setPositiveButton("Sign in with Google") {dialog, which ->
                        startSignInIntent()
                    }
                    .show()
            }
        }
        // Create room button listener
        binding.createRoomButton.setOnClickListener() {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, LobbyFragment())
                .addToBackStack(null)
                .commit()
        }
        // Join room button listener
        binding.joinRoomButton.setOnClickListener() {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, FindLobbyFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.changeVolumeButton.setOnClickListener(){
            if ((this.activity as MainActivity).soundEnabled){
                (this.activity as MainActivity).soundEnabled = false
                binding.changeVolumeButton.setImageResource(R.drawable.ic_volume_off)
            }
            else {
                (this.activity as MainActivity).soundEnabled = true
                binding.changeVolumeButton.setImageResource(R.drawable.ic_volume_up)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setting viewmodel variables
        viewModel.menufragment = this
        viewModel.auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            // Setting up google sign in parameters
            .build()
        // Setting viewmodel variables
        viewModel.googleSignInClient = GoogleSignIn.getClient(this.activity as Activity, gso)
        // Update UI if user is signed in or not
        updateUI(viewModel.auth.currentUser)
    }

    // Google start auth function
    private fun startSignInIntent() {
        enableButtons(false)
        // Init google sign in intent
        val signInIntent = viewModel.googleSignInClient.signInIntent
        // Same as startActivityForResult
        launcher.launch(signInIntent)
    }

    // Same as onActivityResult (Listener)
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Calling viewModel function to get data
            viewModel.googleAuth(result, (activity as Activity), requireContext())
        }
        else {
            enableButtons(true)
        }
    }
    // Sign out function
    private fun googleSignOut() {
        enableButtons(false)
        // Sign out from Google
        viewModel.googleSignInClient.signOut()
        // Sign out from Firebase
        viewModel.auth.signOut()
        // UpdateUI
        updateUI(viewModel.auth.currentUser)
    }
    // Function to Update Username and sign in status on fragment
    fun updateUI(user: FirebaseUser?) {
        // Get user email from firebase
        val username: String = user?.email ?: ""
        // Update username in MainActivity
        (activity as MainActivity).username = username.dropLast(EMAIL_LETTERS_COUNT)
        Log.d(GOOGLETAG, username + " - " + viewModel.auth.currentUser?.email.toString())
        // Update xml dep on sign in status
        val username_formatted = username.dropLast(EMAIL_LETTERS_COUNT)
        viewModel.saveUser(username_formatted)
        checkUser(username_formatted)
        enableButtons(true)
    }
    // Function to update xml dep on sign in status
    private fun checkUser(username: String) {
        if (username.isEmpty()) {
            binding.signInLayoutUnauthorized.root.visibility = View.VISIBLE
            binding.signInLayoutAuthorized.root.visibility = View.INVISIBLE
        } else {
            binding.signInLayoutUnauthorized.root.visibility = View.INVISIBLE
            binding.signInLayoutAuthorized.root.visibility = View.VISIBLE
            val usernameS = getString(
                R.string.hello_string,
                username
            )
            binding.signInLayoutAuthorized.usernameTextview.text = usernameS
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // Animation
    private fun toggleMultiplayerButton() {
        val visibility = binding.joinRoomButton.visibility.xor(XOR_VISIBLE_VALUE_2)
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

    // Enable or disable buttons
    private fun enableButtons(enablebool: Boolean) {
        binding.singleGameButton.isEnabled = enablebool
        binding.statButton.isEnabled = enablebool
        binding.changeVolumeButton.isEnabled = enablebool
        binding.createRoomButton.isEnabled = enablebool
        binding.joinRoomButton.isEnabled = enablebool
        binding.leaderboardButton.isEnabled = enablebool
        binding.multiplayerGameButton.isEnabled = enablebool
    }
    companion object {
        const val XOR_VISIBLE_VALUE_1 = 8
        const val XOR_VISIBLE_VALUE_2 = 4
        const val EMAIL_LETTERS_COUNT = 10
        private const val GOOGLETAG = "GoogleActivity"
    }
}
