package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentLobbyBinding
import com.tmvlg.factorcapgame.ui.multiplayergame.MultiplayerGameFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find.FindLobbyFragment
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
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, FindLobbyFragment.newInstance())
                .commit()
        }
        binding.inviteButton.setOnClickListener {
            Toast.makeText(requireContext(), "Under development", Toast.LENGTH_SHORT).show()
//            requireActivity().supportFragmentManager.beginTransaction()
//                .add(R.id.main_fragment_container, InviteFragment())
//                .addToBackStack(null)
//                .commit()
        }
        listAdapter = LobbyUserListAdapter()
        binding.rvLobbyUsers.adapter = listAdapter
        binding.rvLobbyUsers.layoutManager = LinearLayoutManager(requireContext())
        observeViewModel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        viewModel.stopListenLobby()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveState(outState)
        super.onSaveInstanceState(outState)
    }

    // saves data to bundle
    private fun saveState(outState: Bundle) {
        outState.putString(KEY_LOBBY_ID, lobbyId)
    }

    // loads data from bundle
    private fun loadState(bundle: Bundle) {
        Log.d("1", "loadState: loading")
        lobbyId = bundle.getString(KEY_LOBBY_ID)
            ?: throw IllegalArgumentException("Bundle must contain lobbyId")
    }

    fun observeViewModel() {
        viewModel.lobby.observe(viewLifecycleOwner) { lobby ->
            if (lobby == null) {
                return@observe
            }

            binding.tvLobby.text = lobby.roomName
            listAdapter.submitList(lobby.players)
            // TODO
        }
        viewModel.isHost.observe(viewLifecycleOwner) { isHost ->
            if (isHost) {
                binding.startButton.visibility = View.VISIBLE
                binding.startButton.setOnClickListener {
                    viewModel.startGame()
                    // TODO
                    Toast.makeText(requireContext(), "Under development", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                binding.startButton.visibility = View.GONE
                binding.startButton.setOnClickListener(null)
            }
        }
        viewModel.isGameStarted.observe(viewLifecycleOwner) { isGameStarted ->
            if (isGameStarted) {
                Toast.makeText(requireContext(), "Experimental feature", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, MultiplayerGameFragment.newInstance(lobbyId))
                    .commit()
            }
        }
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
