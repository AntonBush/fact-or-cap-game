package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.data.repository.firebase.Player
import com.tmvlg.factorcapgame.databinding.FragmentLobbyBinding
import com.tmvlg.factorcapgame.ui.menu.MenuFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.MultiplayerGameFragment
import java.lang.IllegalArgumentException

class LobbyFragment : Fragment() {

    private var _binding: FragmentLobbyBinding? = null

    private val binding: FragmentLobbyBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    private lateinit var lobbyId: String
    private lateinit var playerId: String
    private lateinit var playerType: Player.Type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadState(requireArguments())
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
                .replace(R.id.main_fragment_container, MenuFragment())
                .commit()
        }
        binding.inviteButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment_container, InviteFragment())
                .addToBackStack(null)
                .commit()
        }
        if (playerType == Player.Type.HOST) {
            binding.startButton.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, MultiplayerGameFragment())
                    .commit()
            }
        } else {
            binding.startButton.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveState(outState)
        super.onSaveInstanceState(outState)
    }

    // saves data to bundle
    private fun saveState(outState: Bundle) {
        outState.putString(KEY_LOBBY_ID, lobbyId)
        outState.putString(KEY_PLAYER_ID, playerId)
        outState.putInt(KEY_PLAYER_TYPE, playerType.ordinal)
    }

    // loads data from bundle
    private fun loadState(bundle: Bundle) {
        Log.d("1", "loadState: loading")
        lobbyId = bundle.getString(KEY_LOBBY_ID)
            ?: throw IllegalArgumentException("Bundle must contain lobbyId")
        playerId = bundle.getString(KEY_PLAYER_ID)
            ?: throw IllegalArgumentException("Bundle must contain playerId")
        Log.d("1", "lobby: $lobbyId")
        playerType = Player.Type.values()[bundle.getInt(KEY_PLAYER_TYPE)]
    }

    companion object {
        const val KEY_LOBBY_ID = "KEY_LOBBY_ID"
        const val KEY_PLAYER_ID = "KEY_PLAYER_ID"
        const val KEY_PLAYER_TYPE = "KEY_PLAYER_TYPE"

        fun newInstance(lobbyId: String, playerId: String, playerType: Player.Type): LobbyFragment {
            return LobbyFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_LOBBY_ID, lobbyId)
                    putString(KEY_PLAYER_ID, playerId)
                    putInt(KEY_PLAYER_TYPE, playerType.ordinal)
                }
            }
        }
    }
}
