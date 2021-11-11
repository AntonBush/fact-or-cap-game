package com.tmvlg.factorcapgame.ui.multiplayergame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentInviteBinding
import com.tmvlg.factorcapgame.databinding.FragmentLobbyBinding
import com.tmvlg.factorcapgame.ui.menu.MenuFragment


class LobbyFragment : Fragment() {

    private var _binding: FragmentLobbyBinding? = null

    private val binding: FragmentLobbyBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                .replace(R.id.main_fragment_container, InviteFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.startButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, MultiplayerGameFragment())
                .addToBackStack(null)
                .commit()
        }

    }

}