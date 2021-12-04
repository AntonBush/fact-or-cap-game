package com.tmvlg.factorcapgame.ui.multiplayergame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentLobbyBinding
import com.tmvlg.factorcapgame.ui.MainActivity
import com.tmvlg.factorcapgame.ui.menu.MenuFragment
import com.tmvlg.factorcapgame.ui.multiplayergame.lobby.InviteFragment

class LobbyFragment : Fragment() {

    private var _binding: FragmentLobbyBinding? = null

    private val binding: FragmentLobbyBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)

        binding.pageContainer.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_change))

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.returnButton.setOnClickListener {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, MenuFragment())
                .commit()
        }
        binding.inviteButton.setOnClickListener {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment_container, InviteFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.startButton.setOnClickListener {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            // TODO("Получить lobby id")
            val lobbyId = "123456"

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, MultiplayerGameFragment.newInstance(lobbyId))
                .commit()
        }
    }
}
