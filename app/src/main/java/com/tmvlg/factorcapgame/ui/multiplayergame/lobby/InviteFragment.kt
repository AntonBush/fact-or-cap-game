package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentInviteBinding
import com.tmvlg.factorcapgame.ui.MainActivity

class InviteFragment : Fragment() {

    private var _binding: FragmentInviteBinding? = null

    private val binding: FragmentInviteBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInviteBinding.inflate(inflater, container, false)

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
                .replace(R.id.main_fragment_container, LobbyFragment())
                .commit()
        }
        binding.searchButton.setOnClickListener {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            // TODO("Search for friends to invite")
        }
        binding.confirmButton.setOnClickListener {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            // TODO("Invite selected friend")
        }
    }
}
