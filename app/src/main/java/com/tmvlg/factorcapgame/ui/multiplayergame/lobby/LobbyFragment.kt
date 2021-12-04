package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentLobbyBinding
import com.tmvlg.factorcapgame.ui.menu.MenuFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.MultiplayerGameFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find.FindLobbyFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.invite.InviteFragment
import java.lang.IllegalArgumentException

class LobbyFragment : Fragment() {

    private val viewModel: LobbyViewModel by viewModels {
        // inits viewmodel
        val app = activity?.application as FactOrCapApplication
        return@viewModels LobbyViewModelFactory(
            app.firebaseRepository,
            app.userRepository
        )
    }

    private var _binding: FragmentLobbyBinding? = null
    private val binding: FragmentLobbyBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    lateinit var listAdapter: LobbyUserListAdapter

    private lateinit var lobbyId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadState(requireArguments())
        viewModel.listenLobby(lobbyId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.returnButton.setOnClickListener {
            goTo(FindLobbyFragment.newInstance())
        }
        binding.inviteButton.setOnClickListener {
            goTo(InviteFragment.newInstance(lobbyId))
        }
        listAdapter = LobbyUserListAdapter { v ->
            val username = v.findViewById<TextView>(R.id.user_name).text.toString()
            viewModel.removePlayer(username)
        }
        binding.rvLobbyUsers.adapter = listAdapter
        binding.rvLobbyUsers.layoutManager = LinearLayoutManager(requireContext())
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveState(outState)
        super.onSaveInstanceState(outState)
    }

    private fun saveState(outState: Bundle) {
        outState.putString(KEY_LOBBY_ID, lobbyId)
    }

    private fun loadState(bundle: Bundle) {
        lobbyId = bundle.getString(KEY_LOBBY_ID)
            ?: throw IllegalArgumentException("Bundle must contain lobbyId")
    }

    private fun observeViewModel() {
        viewModel.lobby.observe(viewLifecycleOwner) { lobby ->
            if (lobby == null) {
                return@observe
            }
            binding.tvLobby.text = lobby.name
            listAdapter.submitList(lobby.players)
        }
        viewModel.isHost.observe(viewLifecycleOwner) { isHost ->
            if (isHost) {
                binding.startButton.visibility = View.VISIBLE
                binding.startButton.setOnClickListener {
                    viewModel.startGame()
                }
            } else {
                binding.startButton.visibility = View.GONE
                binding.startButton.setOnClickListener(null)
            }
        }
        viewModel.isGameStarted.observe(viewLifecycleOwner) { isGameStarted ->
            if (isGameStarted) {
                goTo(MultiplayerGameFragment.newInstance(lobbyId))
            }
        }
        viewModel.isDisconnected.observe(viewLifecycleOwner) { isDisconnected ->
            if (isDisconnected == true) {
                MaterialAlertDialogBuilder(
                    requireContext(),
                    R.style.ThemeOverlay_App_MaterialAlertDialog
                )
                    .setTitle("Disconnected")
                    .setMessage(
                        "Timeout is out."
                    )
                    .setNeutralButton("Ok") { _, _ -> }
                    .show()
//                goTo(FindLobbyFragment.newInstance())
                goTo(MenuFragment.newInstance())
            }
        }
    }

    private fun goTo(fragment: Fragment) {
        viewModel.stopListenLobby()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .commit()
    }

    companion object {
        const val KEY_LOBBY_ID = "KEY_LOBBY_ID"

        fun newInstance(lobbyId: String): LobbyFragment {
            return LobbyFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_LOBBY_ID, lobbyId)
                }
            }
        }
    }
}
