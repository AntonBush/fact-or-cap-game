package com.tmvlg.factorcapgame.ui.singlegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentSingleGameBinding

class SingleGameFragment : Fragment() {

    private val viewModel: SingleGameViewModel by viewModels {
        val app = activity?.application as FactOrCapApplication
        return@viewModels SingleGameViewModelFactory(
            app.gameRepository,
            app.factRepository,
            app.userRepository
        )
    }

    private var _binding: FragmentSingleGameBinding? = null

    private val binding: FragmentSingleGameBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    private var isEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingleGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.agreeButton.setOnClickListener {
            if (isEnabled) {
                isEnabled = false
                viewModel.sendAnswer(true)
            }
        }
        binding.disagreeButton.setOnClickListener {
            if (isEnabled) {
                isEnabled = false
                viewModel.sendAnswer(false)
            }
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.exception.observe(viewLifecycleOwner) { e ->
            if (e != null) {
                Toast.makeText(this.context, e.message, Toast.LENGTH_LONG).show()
            }
        }
        viewModel.gameFinished.observe(viewLifecycleOwner) { finished ->
            if (finished) {
                endGame()
            }
        }
        viewModel.fact.observe(viewLifecycleOwner) {
            binding.tvFact.text = it.text
            isEnabled = true
        }
        viewModel.rightAnswersCount.observe(viewLifecycleOwner) {
            binding.tvScore.text = it.toString()
        }
    }

    private fun endGame() {
        val score = viewModel.rightAnswersCount.value ?: 0
        val isHighScore = viewModel.isHighScore.value ?: false
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.main_fragment_container,
                SingleGameFinishedFragment.newInstance(
                    score,
                    isHighScore
                )
            )
            .commit()
    }
}
