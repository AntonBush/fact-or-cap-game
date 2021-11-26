package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.data.repository.firebase.Player
import com.tmvlg.factorcapgame.databinding.FragmentLobbyBinding
import com.tmvlg.factorcapgame.ui.menu.MenuFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.MultiplayerGameFinished
import com.tmvlg.factorcapgame.ui.multiplayergame.MultiplayerGameFragment

class LobbyFragment : Fragment() {

    private var _binding: FragmentLobbyBinding? = null

    private val binding: FragmentLobbyBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    private val lobby = MutableLiveData<Lobby>()
    private val playerType = MutableLiveData<Player.Type>()

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
        val pt = playerType.value ?: throw IllegalStateException("playerType is null")
        if (pt == Player.Type.HOST) {
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
        val l = lobby.value
        if (l != null) {
            outState.putParcelable(KEY_LOBBY, l)
        }
        val pt = playerType.value
        if (pt != null) {
            outState.putInt(KEY_PLAYER_TYPE, pt.ordinal)
        }
    }

    // loads data from bundle
    private fun loadState(bundle: Bundle) {
        Log.d("1", "loadState: loading")
        lobby.value = bundle.getParcelable(KEY_LOBBY)
        Log.d("1", "lobby: ${lobby.value}")
        playerType.value = Player.Type.values()[bundle.getInt(KEY_PLAYER_TYPE)]
    }

    companion object {
        const val KEY_LOBBY = "KEY_LOBBY"
        const val KEY_PLAYER_TYPE = "KEY_PLAYER_TYPE"

        fun newInstance(lobby: Lobby, playerType: Player.Type): LobbyFragment {
            return LobbyFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LOBBY, lobby)
                    putInt(KEY_PLAYER_TYPE, playerType.ordinal)
                }
            }
        }
    }
}
