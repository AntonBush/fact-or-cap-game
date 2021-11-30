package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentInviteBinding
import com.tmvlg.factorcapgame.data.network.ApiInterface

import com.tmvlg.factorcapgame.data.network.ApiClient

import com.tmvlg.factorcapgame.data.network.DataModel

import com.tmvlg.factorcapgame.data.network.NotificationModel

import com.tmvlg.factorcapgame.data.network.RootModel
import com.tmvlg.factorcapgame.ui.multiplayergame.MultiplayerGameFinished
import com.tmvlg.factorcapgame.ui.multiplayergame.MultiplayerGameFinishedViewModel
import com.tmvlg.factorcapgame.ui.multiplayergame.MultiplayerGameFinishedViewModelFactory
import com.tmvlg.factorcapgame.ui.multiplayergame.MultiplayerGameFragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        binding.searchButton.setOnClickListener {
            // TODO("Search for friends to invite")
        }
        binding.confirmButton.setOnClickListener {
            // TODO("Invite selected friend")

            val userToBeInvited = binding.findUsersEdittext.text.toString()

            viewModel.invite(userToBeInvited, lobbyId)

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
