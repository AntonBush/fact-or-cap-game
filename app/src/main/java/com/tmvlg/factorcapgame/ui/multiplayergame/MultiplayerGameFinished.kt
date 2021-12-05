package com.tmvlg.factorcapgame.ui.multiplayergame

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentMultiplayerGameFinishedBinding
import com.tmvlg.factorcapgame.ui.MainActivity
import com.tmvlg.factorcapgame.ui.menu.MenuFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find.FindLobbyFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.scoreboard.PlayersScoreboardAdapter

class MultiplayerGameFinished : Fragment() {
    private val viewModel: MultiplayerGameFinishedViewModel by viewModels {
        // inits viewmodel
        val app = activity?.application as FactOrCapApplication
        return@viewModels MultiplayerGameFinishedViewModelFactory(
            app.firebaseGameRepository
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

        binding.pageContainer.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_change))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            loadState(savedInstanceState)
        }
        binding.findLobbyButton.isSoundEffectsEnabled = false
        binding.menuButton.isSoundEffectsEnabled = false
        binding.findLobbyButton.setOnClickListener {
            (activity as MainActivity).snapSEStart()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, FindLobbyFragment.newInstance())
                .commit()
        }
        binding.menuButton.setOnClickListener {
            (activity as MainActivity).snapSEStart()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, MenuFragment.newInstance())
                .commit()
        }
        observeViewModel()
        scoreboardAdapter = PlayersScoreboardAdapter()
        binding.scoreboardRv.adapter = scoreboardAdapter
        binding.scoreboardRv.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewModel.lobbyPlayers.observe(viewLifecycleOwner) {
            Log.d("1", "players in fragment: $it")
            val localScore = score.value ?: throw IllegalStateException("null score")
            val localLobbyId = lobbyId.value ?: throw IllegalStateException("null lobbyId")
            if (it.isNotEmpty()) {
                viewModel.sendScore(localScore, localLobbyId)
                if (viewModel.isAllFinished()) {
                    binding.tvWaiting.text = "Game results"
                    val winner = viewModel.getWinner()
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
            viewModel.connectToLobby(it)
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
        val localLobbyId = lobbyId.value ?: throw IllegalStateException("can't fetch lobbyId")
        viewModel.finish(localLobbyId)
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
