package com.tmvlg.factorcapgame.ui.multiplayergame

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentMultiplayerGameBinding
import com.tmvlg.factorcapgame.ui.MainActivity
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

    // check for fact is shown, enables agree and disagree buttons if true
    private var isEnabled = false

    private var congratsArray = arrayOfNulls<String>(0)

    private var _binding: FragmentMultiplayerGameBinding? = null
    private val binding: FragmentMultiplayerGameBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    private val lobbyId = MutableLiveData<String>()

    // check for fact is shown, enables agree and disagree buttons if true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("1", "loadState: loading")
        lobbyId.value = requireArguments().getString(KEY_LOBBY_ID)
            ?: throw IllegalArgumentException("Bundle must contain lobbyId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMultiplayerGameBinding.inflate(inflater, container, false)

        binding.pageContainer.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.fragment_change
            )
        )

        congratsArray = arrayOf(
            getString(R.string.correct_answer_string_1),
            getString(R.string.correct_answer_string_2),
            getString(R.string.correct_answer_string_3),
            getString(R.string.correct_answer_string_4),
            getString(R.string.correct_answer_string_5),
            getString(R.string.correct_answer_string_6),
            getString(R.string.correct_answer_string_7),
            getString(R.string.correct_answer_string_8),
            getString(R.string.correct_answer_string_9),
            getString(R.string.correct_answer_string_10)
        )

        return binding.root
    }

    // all the bindings here
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.agreeButton.isSoundEffectsEnabled = false
        binding.disagreeButton.isSoundEffectsEnabled = false
        setupListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val lid = lobbyId.value
        if (lid != null) {
            outState.putString(KEY_LOBBY_ID, lid)
        }
        super.onSaveInstanceState(outState)
    }

    private fun setupListeners() {
        // sends agree answer if button is enabled
        binding.agreeButton.setOnClickListener {
            if (isEnabled) {
                isEnabled = false
                (activity as MainActivity).snapSEStart()
                binding.tvFact.visibility = View.INVISIBLE
                viewModel.sendAnswer(true)
            }
        }
        // sends disagree answer if button is enabled
        binding.disagreeButton.setOnClickListener {
            if (isEnabled) {
                isEnabled = false
                (activity as MainActivity).snapSEStart()
                binding.tvFact.visibility = View.INVISIBLE
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
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
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
            binding.tvFact.visibility = View.VISIBLE
            isEnabled = true
        }
        // right answers counter
        viewModel.rightAnswersCount.observe(viewLifecycleOwner) {
            _binding?.tvScore?.text = it.toString()
//            startAnimator(
//                    _binding,
//                    getColor(R.color.online_indicator_color, requireContext()),
//                    getColor(R.color.font_color, requireContext()),
//                    RIGHT_ANSWER_ANIMATION_DURATION
//            )
        }
        viewModel.factsLoadingState.observe(viewLifecycleOwner) { isStillLoading ->
            if (!isStillLoading) {
                _binding?.scoreContainer?.visibility = View.VISIBLE
                _binding?.agreeButton?.visibility = View.VISIBLE
                _binding?.disagreeButton?.visibility = View.VISIBLE
            }
        }
        viewModel.isAnswerCorrect.observe(viewLifecycleOwner) { isCorrect ->
            Log.d("1", "answer coorect = : $isCorrect")

            if (isCorrect) {
                funkyAnimationCorrect()
            }

            val typedValue = TypedValue()
            val theme = requireContext().theme

            theme.resolveAttribute(R.attr.colorSecondaryVariant, typedValue, true)
            @ColorInt val colorCorrect = typedValue.data

            theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
            @ColorInt val colorIncorrect = typedValue.data

            val startColor = if (isCorrect) {
                colorCorrect
            } else {
                colorIncorrect
            }
            Log.d("1", "answer color: $startColor")

            theme.resolveAttribute(R.attr.textAppearanceHeadline2, typedValue, true)
            @ColorInt val endColor = typedValue.data

            startAnimator(
                _binding,
                startColor,
                endColor,
                IS_ANSWER_CORRECT_ANIMATION_DURATION
            )
        }
        // binds how much time left
        viewModel.timeLeftFormatted.observe(viewLifecycleOwner) {
            _binding?.tvTimer?.text = it
        }
    }

    private fun funkyAnimationCorrect() {
        (activity as MainActivity).correctSEStart()
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.game_correct_answer)
        binding.singleGameAnimationText.text = congratsArray.random()
        binding.singleGameAnimationImage.startAnimation(animation)
        binding.singleGameAnimationText.startAnimation(animation)
        binding.singleGameAnimationImage.visibility = View.INVISIBLE
        binding.singleGameAnimationText.visibility = View.INVISIBLE
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
        const val TAG = "MultiplayerGameFragment"
        const val KEY_LOBBY_ID = "KEY_LOBBY_ID"
        const val RIGHT_ANSWER_ANIMATION_DURATION = 200L
        const val IS_ANSWER_CORRECT_ANIMATION_DURATION = 2_000L

        private fun startAnimator(
            binding: FragmentMultiplayerGameBinding?,
            startColor: Int,
            endColor: Int,
            duration: Long
        ) {
            try {
                val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
                animator.addUpdateListener { anim ->
                    binding?.tvTimer?.setTextColor(
                        ColorUtils.blendARGB(
                            startColor,
                            endColor,
                            anim.animatedFraction
                        )
                    )
                }
                animator.setDuration(duration).start()
            } catch (e: IllegalStateException) {
                Log.w(TAG, e.message ?: "")
            }
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
