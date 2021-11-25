package com.tmvlg.factorcapgame.ui.multiplayergame

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentMultiplayerGameFinishedBinding
import com.tmvlg.factorcapgame.ui.menu.MenuFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.LobbyFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.scoreboard.PlayersScoreboardAdapter
import com.tmvlg.factorcapgame.ui.singlegame.SingleGameFinishedFragment
import java.lang.IllegalArgumentException
import java.lang.RuntimeException

class MultiplayerGameFinished : Fragment() {

    private val viewModel: MultiplayerGameFinishedViewModel by viewModels {
        // inits viewmodel
        val app = activity?.application as FactOrCapApplication
        return@viewModels MultiplayerGameFinishedViewModelFactory(
            app.firebaseRepository,
            app.userRepository
        )
    }
    private var _binding: FragmentMultiplayerGameFinishedBinding? = null

    private val binding: FragmentMultiplayerGameFinishedBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    private lateinit var scoreboardAdapter: PlayersScoreboardAdapter

    val score = MutableLiveData<Int>()

    val lobbyId = MutableLiveData<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadState(requireArguments())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMultiplayerGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            loadState(savedInstanceState)
        }
        binding.restartButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, LobbyFragment())
                .commit()
        }
        binding.menuButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, MenuFragment())
                .commit()
        }
        observeViewModel()
        scoreboardAdapter = PlayersScoreboardAdapter()
        binding.scoreboardRv.adapter = scoreboardAdapter
        binding.scoreboardRv.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewModel.lobbyPlayers.observe(viewLifecycleOwner) {
            Log.d("1", "players in fragment: $it")
            val _score = score.value ?: throw RuntimeException("null score")
            val _lobbyId = lobbyId.value ?: throw RuntimeException("null lobbyId")
            if (it.isNotEmpty()) {
                viewModel.sendScore(_score, _lobbyId)
                if (viewModel.isAllFinished()) {
                    binding.tvWaiting.text = "Game results"
                    val winner = viewModel.getWinner(_lobbyId)
                    if (winner == viewModel.getUsername()) {
                        binding.tvGameResult.text = "You win!"
                    } else {
                        binding.tvGameResult.text = "You lose!"
                    }
                }
            }
            scoreboardAdapter.submitList(it)
        }
        lobbyId.observe(viewLifecycleOwner) {
            viewModel.lobbies(it)
        }
        score.observe(viewLifecycleOwner) {

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveState(outState)
    }

    // saves data to bundle
    private fun saveState(outState: Bundle) {
        val sc = score.value
        if (sc != null) {
            outState.putInt(KEY_SCORE, sc)
        }
        val lid = lobbyId.value
        if (lid != null) {
            outState.putString(KEY_LOBBY_ID, lid)
        }
    }

    // loads data from bundle
    private fun loadState(bundle: Bundle) {
        Log.d("1", "loadState: loading")
        score.value = bundle.getInt(KEY_SCORE)
        lobbyId.value = bundle.getString(KEY_LOBBY_ID)
        Log.d("1", "score: ${score.value}")
        Log.d("1", "lobbyId: ${lobbyId.value}")

    }

    override fun onDestroy() {
        super.onDestroy()
        val _lobbyId = lobbyId.value ?: throw RuntimeException("can't fetch lobbyId")
        viewModel.finish(_lobbyId)
    }

    companion object {
        const val KEY_SCORE = "multiplayer_score"
        const val KEY_LOBBY_ID = "lobby_id"

        // calls from MultiplayerGameFragment.kt to create this fragment and pass arguments
        fun newInstance(score: Int, lobbyId: String): MultiplayerGameFinished {
            return MultiplayerGameFinished().apply {
                arguments = Bundle().apply {
                    putInt(KEY_SCORE, score)
                    putString(KEY_LOBBY_ID, lobbyId)
                }
            }
        }
    }
}
