package com.tmvlg.factorcapgame.ui.singlegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentSingleGameBinding
import com.tmvlg.factorcapgame.ui.MainActivity
import com.tmvlg.factorcapgame.ui.NoInternetFragment
import kotlinx.coroutines.delay
import kotlin.random.Random

class SingleGameFragment : Fragment() {

    private val viewModel: SingleGameViewModel by viewModels{
        // inits viewmodel
        val app = activity?.application as FactOrCapApplication
        return@viewModels SingleGameViewModelFactory(
            app.gameRepository,
            app.factRepository,
            app.userRepository,
            this
        )
    }

    private var _binding: FragmentSingleGameBinding? = null

    private val binding: FragmentSingleGameBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    // check for fact is shown, enables agree and disagree buttons if true
    private var isEnabled = false

    private var congratsArray = arrayOfNulls<String>(0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingleGameBinding.inflate(inflater, container, false)

        binding.gameContainer.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_change))

        congratsArray = arrayOf(getString(R.string.correct_answer_string_1),
            getString(R.string.correct_answer_string_2),
            getString(R.string.correct_answer_string_3),
            getString(R.string.correct_answer_string_4),
            getString(R.string.correct_answer_string_5),
            getString(R.string.correct_answer_string_6),
            getString(R.string.correct_answer_string_7),
            getString(R.string.correct_answer_string_8),
            getString(R.string.correct_answer_string_9),
            getString(R.string.correct_answer_string_10))

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // all the bindings here
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // sends agree answer if button is enabled
        binding.agreeButton.setOnClickListener {
            if (isEnabled) {
                binding.tvFact.visibility = View.INVISIBLE
                isEnabled = false
                viewModel.sendAnswer(true)
            }
        }
        // sends disagree answer if button is enabled
        binding.disagreeButton.setOnClickListener {
            if (isEnabled) {
                binding.tvFact.visibility = View.INVISIBLE
                isEnabled = false
                viewModel.sendAnswer(false)
            }
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        // throws exception if can't fetch a fact for some reason
        viewModel.exception.observe(viewLifecycleOwner) { e ->
            if (e != null) {
                Toast.makeText(this.context, e.message, Toast.LENGTH_LONG).show()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, NoInternetFragment())
                    .commit()
            }
        }
        // observes if game finished (wrong answer)
        viewModel.gameFinished.observe(viewLifecycleOwner) { finished ->
            if (finished) {
                endGame()
            }
        }
        // bind new fact
        viewModel.fact.observe(viewLifecycleOwner) {
            binding.tvFact.text = it.text
            isEnabled = true
        }
        // right answers counter
        viewModel.rightAnswersCount.observe(viewLifecycleOwner) {
            binding.tvScore.text = it.toString()
        }
    }

    fun funkyAnimationCorrect(){
        if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).correctSE.start()
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.game_correct_answer)
        binding.singleGameAnimationText.text = congratsArray.random()
        binding.singleGameAnimationImage.startAnimation(animation)
        binding.singleGameAnimationText.startAnimation(animation)
        binding.singleGameAnimationImage.visibility = View.INVISIBLE
        binding.singleGameAnimationText.visibility = View.INVISIBLE
    }

    fun funkyAnimationWrong(){
        if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).wrongSE.start()
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.game_wrong_answer)
        binding.gameContainer.startAnimation(animation)
    }

    // calls when game is finished. Goes to finish fragment with score results
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

    fun hideText() {
        binding.tvFact.visibility = View.INVISIBLE
    }
    fun showText() {
        binding.tvFact.visibility = View.VISIBLE
    }
}
