package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.invite

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentInviteBinding
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.LobbyFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.invite.userslist.SearchedUsersAdapter
import java.lang.IllegalArgumentException

class InviteFragment : Fragment() {

    private var _binding: FragmentInviteBinding? = null

    private val binding: FragmentInviteBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    private val viewModel: InviteFragmentViewModel by viewModels {
        // inits viewmodel
        val app = activity?.application as FactOrCapApplication
        return@viewModels InviteFragmentViewModelFactory(
            app.userRepository
        )
    }

    private lateinit var lobbyId: String

    private lateinit var searchedUsersAdapter: SearchedUsersAdapter

    private var selectedPlayerName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadState(requireArguments())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInviteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.clearPlayerList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            loadState(savedInstanceState)
        }
        binding.returnButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, LobbyFragment.newInstance(lobbyId))
                .commit()
        }

        binding.confirmButton.setOnClickListener {
            if (selectedPlayerName != null) {
                val userToBeInvited = selectedPlayerName!!
                viewModel.invite(userToBeInvited, lobbyId)
            } else {
                Toast.makeText(requireContext(), "Select a friend first", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.searchButton.setOnClickListener {
            val query = binding.findUsersEdittext.text
            viewModel.findPlayers(query.toString())
            try {
                val imm = requireActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
                imm.hideSoftInputFromWindow(
                    requireActivity().currentFocus?.windowToken,
                    0
                )
            } catch (e: IllegalStateException) {
                TODO("Handle exception")
            }
        }

        searchedUsersAdapter = SearchedUsersAdapter()
        binding.rvFoundUsers.adapter = searchedUsersAdapter
        binding.rvFoundUsers.layoutManager = LinearLayoutManager(requireContext())

        searchedUsersAdapter.onSearchedUserClickListener = {
            Log.d("1", "onViewCreated: $it")
            selectedPlayerName = it.name
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.searchedPlayers.observe(viewLifecycleOwner) {
            Log.d("1", "getPlayers: obseved $it")
            searchedUsersAdapter.submitList(it)
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

        fun newInstance(lobbyId: String): InviteFragment {
            return InviteFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_LOBBY_ID, lobbyId)
                }
            }
        }
    }
}
