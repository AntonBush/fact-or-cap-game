package com.tmvlg.factorcapgame.ui.multiplayergame

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentMultiplayerGameBinding
import java.lang.IllegalArgumentException

class MultiplayerGameFragment : Fragment() {

    private val viewModel: MultiplayerGameViewModel by viewModels {
        // inits viewmodel
        val app = activity?.application as FactOrCapApplication
        return@viewModels MultiplayerGameViewModelFactory(
            app.factRepository,
            app.firebaseGameRepository
        )
    }

    private var _binding: FragmentMultiplayerGameBinding? = null
    private val binding: FragmentMultiplayerGameBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    private val lobbyId = MutableLiveData<String>()

    // check for fact is shown, enables agree and disagree buttons if true
    private var isEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadState(requireArguments())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMultiplayerGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    // all the bindings here
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveState(outState)
        super.onSaveInstanceState(outState)
    }

    // saves data to bundle
    private fun saveState(outState: Bundle) {
        val lid = lobbyId.value
        if (lid != null) {
            outState.putString(KEY_LOBBY_ID, lid)
        }
    }

    // loads data from bundle
    private fun loadState(bundle: Bundle) {
        Log.d("1", "loadState: loading")
        lobbyId.postValue(
            bundle.getString(KEY_LOBBY_ID)
                ?: throw IllegalArgumentException("Bundle must contain lobbyId")
        )
    }

    private fun setupListeners() {
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

        lobbyId.observe(viewLifecycleOwner) {
            viewModel.startGame(it)
        }
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
                isEnabled = false
                endGame()
            }
        }
        // bind new fact
        viewModel.fact.observe(viewLifecycleOwner) {
            _binding?.tvFact?.text = it.text
            isEnabled = true
        }
        // right answers counter
        viewModel.rightAnswersCount.observe(viewLifecycleOwner) {
            _binding?.tvScore?.text = it.toString()
            val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
            animator.addUpdateListener { anim ->
                _binding?.tvTimer?.setTextColor(
                    ColorUtils.blendARGB(
                        getColor(R.color.online_indicator_color, requireContext()),
                        getColor(R.color.font_color, requireContext()),
                        anim.animatedFraction
                    )
                )
            }
            animator.setDuration(RIGHT_ANSWER_ANIMATION_DURATION).start()
        }
        viewModel.factsLoadingState.observe(viewLifecycleOwner) { isStillLoading ->
            if (!isStillLoading) {
                _binding?.scoreContainer?.visibility = View.VISIBLE
                _binding?.agreeButton?.visibility = View.VISIBLE
                _binding?.disagreeButton?.visibility = View.VISIBLE
            }
        }
        viewModel.isAnswerCorrect.observe(viewLifecycleOwner) { isCorrect ->
            val startColor = if (isCorrect) {
                getColor(R.color.primary_red, requireContext())
            } else {
                getColor(R.color.online_indicator_color, requireContext())
            }
            val endColor: Int = getColor(R.color.font_color, requireContext())
            val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
            animator.addUpdateListener { anim ->
                _binding?.tvTimer?.setTextColor(
                    ColorUtils.blendARGB(
                        startColor,
                        endColor,
                        anim.animatedFraction
                    )
                )
            }
            animator.setDuration(IS_ANSWER_CORRECT_ANIMATION_DURATION).start()
        }
        // binds how much time left
        viewModel.timeLeftFormatted.observe(viewLifecycleOwner) {
            _binding?.tvTimer?.text = it
        }
    }

    // calls when game is finished. Goes to finish fragment with score results
    private fun endGame() {
        val score = viewModel.rightAnswersCount.value ?: 0
        val lobbyIdValue: String =
            lobbyId.value ?: throw IllegalStateException("can't fetch lobby id")
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.main_fragment_container,
                MultiplayerGameFinished.newInstance(
                    score,
                    lobbyIdValue
                )
            )
            .commit()
    }

    companion object {
        const val KEY_LOBBY_ID = "KEY_LOBBY_ID"
        const val RIGHT_ANSWER_ANIMATION_DURATION = 200L
        const val IS_ANSWER_CORRECT_ANIMATION_DURATION = 2_000L

        private fun getColor(colorId: Int, context: Context): Int {
            return AppCompatResources.getColorStateList(
                context,
                colorId
            ).defaultColor
        }

        // calls from MultiplayerGameFragment.kt to create this fragment
        fun newInstance(lobbyId: String): MultiplayerGameFragment {
            return MultiplayerGameFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_LOBBY_ID, lobbyId)
                }
            }
        }
    }
}
