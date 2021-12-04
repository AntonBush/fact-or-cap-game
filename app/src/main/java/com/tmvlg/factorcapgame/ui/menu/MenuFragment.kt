package com.tmvlg.factorcapgame.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.CreateRoomDialogBinding
import com.tmvlg.factorcapgame.databinding.FragmentMenuBinding
import com.tmvlg.factorcapgame.ui.leaderboard.LeaderboardFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.LobbyFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find.FindLobbyFragment
import com.tmvlg.factorcapgame.ui.singlegame.SingleGameFragment
import com.tmvlg.factorcapgame.ui.statisitics.StatisticsFragment
import android.animation.ValueAnimator.*
import com.tmvlg.factorcapgame.ui.MainActivity

class MenuFragment : Fragment() {

    private val viewModel: MenuViewModel by viewModels {
        // inits viewmodel
        val app = activity?.application as FactOrCapApplication
        return@viewModels MenuViewModelFactory(
            app.userRepository,
            app.firebaseRepository
        )
    }

    private var _binding: FragmentMenuBinding? = null
    private val binding: FragmentMenuBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    private var isEnabled: Boolean
        get() = binding.root.isEnabled
        set(value) {
            binding.apply {
                changeVolumeButton.isEnabled = value
                createRoomButton.isEnabled = value
                gameButtonsLl.isEnabled = value
                joinRoomButton.isEnabled = value
                leaderboardButton.isEnabled = value
                multiplayerGameButton.isEnabled = value
                statButton.isEnabled = value
                singleGameButton.isEnabled = value
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate after super.onCreate")
        viewModel.initializeAuth(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        binding.pageContainer.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_change))

        if (!(this.activity as MainActivity).soundEnabled) binding.changeVolumeButton.setImageResource(R.drawable.ic_volume_off)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated after super.onViewCreated")
        setupListeners()
        observeViewModel()
        Log.d(TAG, "onViewCreated end")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        // Sign in button listener for toggling sign out button visibility
        binding.signInLayoutAuthorized.signedUserCardview.setOnClickListener {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            val signOutLayout = binding.signInLayoutAuthorized.signOutLayout
            when (signOutLayout.visibility) {
                View.GONE -> signOutLayout.visibility = View.VISIBLE
                View.VISIBLE -> signOutLayout.visibility = View.GONE
                else -> signOutLayout.visibility = View.GONE
            }
        }
        // Sign in button listener
        binding.signInLayoutUnauthorized.googleSignInCardview.setOnClickListener {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            isEnabled = false
            // Calling sign in function
            signIn()
        }
        // Sign out button listener
        binding.signInLayoutAuthorized.signOutButton.setOnClickListener {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            // Calling sign out function
            signOut()
        }
        // Start single game button listener
        binding.singleGameButton.setOnClickListener() {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            isEnabled = false
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, SingleGameFragment())
                .commit()
        }
        // Multiplayer game button listener
        binding.multiplayerGameButton.setOnClickListener() {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            isEnabled = false
            // Calling toggle mpb function
            Log.d("-----------------------", "${viewModel.isUserSignedIn.value}:${viewModel.isUserSignedIn.value == true}")
            if (viewModel.isUserSignedIn.value == true) {
                toggleMultiplayerButton()
                Log.d("-------------------tog-", "")
                isEnabled = true
            } else {
                MaterialAlertDialogBuilder(
                    requireContext(),
                    R.style.ThemeOverlay_App_MaterialAlertDialog
                )
                    .setTitle("Auth required")
                    .setMessage(
                        "You are not authorized yet." +
                            " You should authorize via Google to play multiplayer." +
                            " Do you want to proceed?"
                    )
                    .setNegativeButton("Cancel") { dialog, which ->
                        Log.d("-------------------can-", "")
                        isEnabled = true
                        dialog.cancel()
                    }
                    .setPositiveButton("Sign in with Google") { dialog, which ->
                        signIn()
                    }
                    .show()
            }
        }
        // Create room button listener
        binding.createRoomButton.setOnClickListener() {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            isEnabled = false
            val b = CreateRoomDialogBinding.inflate(LayoutInflater.from(requireContext()))
            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog
            )
                .setTitle("Auth required")
                .setMessage(
                    "You are going to create new lobby." +
                        " You should enter the name of new room to play multiplayer." +
                        " Do you want to proceed?"
                )
                .setView(b.root)
                .setNegativeButton("Cancel") { dialog, which ->
                    isEnabled = true
                    dialog.cancel()
                }
                .setPositiveButton("Create room") { dialog, which ->
                    viewModel.createLobby(b.editRoomName.text.toString())
                }
                .show()
        }
        // Join room button listener
        binding.joinRoomButton.setOnClickListener() {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, FindLobbyFragment.newInstance())
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

        // Statistics button listener
        binding.statButton.setOnClickListener() {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, StatisticsFragment())
                .commit()
        }
        // Leader button listener
        binding.leaderboardButton.setOnClickListener() {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, LeaderboardFragment())
                .commit()
//            Toast.makeText(this.context, "In development", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
//        viewModel.user.observe(viewLifecycleOwner) { isEnabled = true }
        viewModel.username.observe(viewLifecycleOwner) { username ->
            binding.signInLayoutAuthorized.usernameTextview.text = getString(
                R.string.hello_string,
                username ?: ""
            )
        }
        viewModel.isUserSignedIn.observe(viewLifecycleOwner) { isUserSignedIn ->
            if (isUserSignedIn) {
                binding.signInLayoutUnauthorized.root.visibility = View.INVISIBLE

                binding.signInLayoutAuthorized.root.visibility = View.VISIBLE
                binding.signInLayoutAuthorized.signOutButton.visibility = View.VISIBLE
            } else {
                binding.signInLayoutAuthorized.signOutLayout.visibility = View.GONE
                binding.signInLayoutAuthorized.root.visibility = View.INVISIBLE

                binding.signInLayoutUnauthorized.root.visibility = View.VISIBLE
            }

            Log.d("----------------------1", "$isUserSignedIn")
            Log.d("-----------------------", "${viewModel.isUserSignedIn.value}:${viewModel.isUserSignedIn.value == true}")
            isEnabled = true
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                Log.d("-------------------err-", "")
                isEnabled = true
            }
        }
        viewModel.createdLobbyId.observe(viewLifecycleOwner) { lobbyId ->
            if (lobbyId == null) {
                return@observe
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.main_fragment_container,
                    LobbyFragment.newInstance(lobbyId)
                )
                .commit()
        }
    }

    private fun signIn() {
        isEnabled = false
        viewModel.startSignInIntent(launcher)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> viewModel.signInFromIntent(result, requireActivity()) }

    private fun signOut() {
        isEnabled = false
        viewModel.signOut()
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

    companion object {
        const val XOR_VISIBLE_VALUE_2 = 4
        private const val TAG = "MenuFragment"

        fun newInstance(): MenuFragment {
            return MenuFragment()
        }
    }
}
