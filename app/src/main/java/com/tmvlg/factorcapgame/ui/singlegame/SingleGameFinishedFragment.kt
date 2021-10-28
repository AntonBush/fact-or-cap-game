package com.tmvlg.factorcapgame.ui.singlegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.databinding.FragmentSingleGameBinding
import com.tmvlg.factorcapgame.databinding.FragmentSingleGameFinishedBinding

class SingleGameFinishedFragment : Fragment() {

    private lateinit var viewModel: SingleGameViewModel

    private var _binding: FragmentSingleGameFinishedBinding? = null
    private val binding: FragmentSingleGameFinishedBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    private val singleGameViewModelFactory by lazy {
        SingleGameViewModelFactory(
            (activity?.application as FactOrCapApplication).gameRepository,
            (activity?.application as FactOrCapApplication).factRepository,
            (activity?.application as FactOrCapApplication).userRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    private var score: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSingleGameFinishedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            singleGameViewModelFactory
        )[SingleGameViewModel::class.java]

        observeViewModel()

        viewModel.saveStats(score = score)

        binding.tvScorePoints.text = "$score Points"

    }

    private fun observeViewModel() {
        viewModel.isHighScore.observe(viewLifecycleOwner) {
            binding.tvHighscore.visibility = View.VISIBLE
        }
    }

    private fun parseArgs() {
        score = requireArguments().getInt(KEY_SCORE)
    }

    companion object {

        const val KEY_SCORE = "score"

        fun newInstance(score: Int): SingleGameFinishedFragment {
            return SingleGameFinishedFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_SCORE, score)
                }
            }
        }
    }
}
