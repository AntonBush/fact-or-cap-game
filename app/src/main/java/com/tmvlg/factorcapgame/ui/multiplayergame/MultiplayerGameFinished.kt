package com.tmvlg.factorcapgame.ui.multiplayergame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentMultiplayerGameBinding
import com.tmvlg.factorcapgame.databinding.FragmentMultiplayerGameFinishedBinding

class MultiplayerGameFinished : Fragment() {

    private var _binding: FragmentMultiplayerGameFinishedBinding? = null

    private val binding: FragmentMultiplayerGameFinishedBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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

    }

}