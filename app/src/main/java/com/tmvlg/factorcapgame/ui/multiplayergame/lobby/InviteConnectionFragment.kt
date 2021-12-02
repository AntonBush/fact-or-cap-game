package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentInviteBinding
import com.tmvlg.factorcapgame.databinding.FragmentInviteConnectionBinding
import com.tmvlg.factorcapgame.ui.NoInternetFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.MultiplayerGameFinished
import java.lang.IllegalArgumentException

class InviteConnectionFragment : Fragment() {

    private var _binding: FragmentInviteConnectionBinding? = null

    private val binding: FragmentInviteConnectionBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    private val viewModel: InviteConnectionFragmentViewModel by viewModels {
        // inits viewmodel
        val app = activity?.application as FactOrCapApplication
        return@viewModels InviteConnectionFragmentViewModelFactory(
            app.firebaseRepository,
            app.userRepository
        )
    }

    private lateinit var lobbyId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadState(requireArguments())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInviteConnectionBinding.inflate(inflater, container, false)
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
        observeViewModel()
        viewModel.setup(lobbyId)
    }

    private fun observeViewModel() {
        viewModel.readyToConnect.observe(viewLifecycleOwner) { isReady ->
            if (isReady) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, LobbyFragment.newInstance(lobbyId))
                    .commit()
            }
        }
        viewModel.exception.observe(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, NoInternetFragment())
                .commit()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveState(outState)
    }

    // saves data to bundle
    private fun saveState(outState: Bundle) {
        outState.putString(KEY_LOBBY_ID, lobbyId)
    }

    // loads data from bundle
    private fun loadState(bundle: Bundle) {
        lobbyId = bundle.getString(KEY_LOBBY_ID)
            ?: throw IllegalArgumentException("Bundle must contain lobbyId")
    }


    companion object {
        const val KEY_LOBBY_ID = "KEY_LOBBY_ID"

        fun newInstance(lobbyId: String): InviteConnectionFragment {
            return InviteConnectionFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_LOBBY_ID, lobbyId)
                }
            }
        }
    }


}