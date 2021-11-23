package com.tmvlg.factorcapgame.ui.multiplayergame

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentMultiplayerGameBinding

class MultiplayerGameFragment : Fragment() {

    private val viewModel: MultiplayerGameViewModel by viewModels {

        // inits viewmodel
        val app = activity?.application as FactOrCapApplication
        return@viewModels MultiplayerGameViewModelFactory(
            app.gameRepository,
            app.factRepository,
            app.userRepository
        )
    }

    // check for fact is shown, enables agree and disagree buttons if true
    private var isEnabled = false

    private var _binding: FragmentMultiplayerGameBinding? = null

    private val binding: FragmentMultiplayerGameBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMultiplayerGameBinding.inflate(inflater, container, false)
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
                isEnabled = false
                viewModel.sendAnswer(true)
            }
        }

        // sends disagree answer if button is enabled
        binding.disagreeButton.setOnClickListener {
            if (isEnabled) {
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
            }
        }

        // observes if game finished (timer is 00:00)
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
            val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
            animator.addUpdateListener { anim ->
                binding.tvTimer.setTextColor(
                    ColorUtils.blendARGB(
                        resources.getColor(R.color.online_indicator_color),
                        resources.getColor(R.color.font_color),
                        anim.animatedFraction
                    )
                )
            }
            animator.setDuration(200).start()

        }

        viewModel.isAnswerCorrect.observe(viewLifecycleOwner) { isCorrect ->
            Log.d("1", "answer coorect = : $isCorrect")
            var startColor = if (isCorrect)
                resources.getColor(R.color.primary_red) else
                resources.getColor(R.color.online_indicator_color)
            Log.d("1", "answer color: $startColor")
            var endColor: Int = resources.getColor(R.color.font_color)
            val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
            animator.addUpdateListener { anim ->
                binding.tvTimer.setTextColor(
                    ColorUtils.blendARGB(
                        startColor,
                        endColor,
                        anim.animatedFraction
                    )
                )
            }
            animator.setDuration(2000).start()
        }

        // binds how much time left
        viewModel.timeLeftFormatted.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it
        }
    }

    // calls when game is finished. Goes to finish fragment with score results
    private fun endGame() {
        val score = viewModel.rightAnswersCount.value ?: 0
        // TODO("Получить lobbyId")
        val lobbyId = "123456"
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.main_fragment_container,
                MultiplayerGameFinished.newInstance(
                    score,
                    lobbyId
                )
            )
            .commit()
    }
}
