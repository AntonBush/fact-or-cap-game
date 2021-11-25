package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentFindLobbyBinding
import com.tmvlg.factorcapgame.ui.menu.MenuFragment

class FindLobbyFragment : Fragment() {

    private val viewModel: FindLobbyViewModel by viewModels {
        // inits viewmodel
        val app = activity?.application as FactOrCapApplication
        return@viewModels FindLobbyViewModelFactory(
            app.firebaseRepository,
            app.userRepository
        )
    }

    private var _binding: FragmentFindLobbyBinding? = null

    private val binding: FragmentFindLobbyBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    private lateinit var lobbiesListAdapter: LobbiesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.listenLobbies()
//        loadState(requireArguments())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindLobbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.returnButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, MenuFragment())
                .commit()
        }
        binding.joinButton.setOnClickListener {
            // TODO("Join lobby")
        }
//        if (savedInstanceState != null) {
//            loadState(savedInstanceState)
//        }
        lobbiesListAdapter = LobbiesListAdapter()
        binding.findLobbyRecyclerview.adapter = lobbiesListAdapter
        binding.findLobbyRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        observeViewModel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        viewModel.stopListenLobbies()
        super.onDestroy()
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        saveState(outState)
//    }

    // saves data to bundle
//    private fun saveState(outState: Bundle) {
//        val sc = score.value
//        if (sc != null) {
//            outState.putInt(KEY_SCORE, sc)
//        }
//        val lid = lobbyId.value
//        if (lid != null) {
//            outState.putString(KEY_LOBBY_ID, lid)
//        }
//    }

    // loads data from bundle
//    private fun loadState(bundle: Bundle) {
//        Log.d("1", "loadState: loading")
//        score.value = bundle.getInt(KEY_SCORE)
//        lobbyId.value = bundle.getString(KEY_LOBBY_ID)
//        Log.d("1", "score: ${score.value}")
//        Log.d("1", "lobbyId: ${lobbyId.value}")
//
//    }

    private fun observeViewModel() {
        viewModel.lobbies.observe(viewLifecycleOwner) {
            Log.d(tag, "lobbies in fragment: $it")
            lobbiesListAdapter.submitList(it)
        }
    }

    companion object {
        fun newInstance(score: Int, lobbyId: String): FindLobbyFragment {
            return FindLobbyFragment()
        }
    }
}
