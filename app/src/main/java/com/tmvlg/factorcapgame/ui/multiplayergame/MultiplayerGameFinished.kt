package com.tmvlg.factorcapgame.ui.multiplayergame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentMultiplayerGameFinishedBinding
import com.tmvlg.factorcapgame.ui.menu.MenuFragment

class MultiplayerGameFinished : Fragment() {

    private var _binding: FragmentMultiplayerGameFinishedBinding? = null

    private val binding: FragmentMultiplayerGameFinishedBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

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
    }

    companion object {
        const val KEY_SCORE = "score"

        // calls from MultiplayerGameFragment.kt to create this fragment and pass arguments
        fun newInstance(score: Int): MultiplayerGameFinished {
            return MultiplayerGameFinished().apply {
                arguments = Bundle().apply {
                    putInt(KEY_SCORE, score)
                }
            }
        }
    }
}
