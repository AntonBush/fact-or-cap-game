package com.tmvlg.factorcapgame.ui.singlegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentSingleGameFinishedBinding
import com.tmvlg.factorcapgame.ui.menu.MenuFragment

class SingleGameFinishedFragment : Fragment() {
    private var _binding: FragmentSingleGameFinishedBinding? = null

    private val binding: FragmentSingleGameFinishedBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    private val score = MutableLiveData(0)
    private val isHighScore = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadState(requireArguments())
    }

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

    // all the bindings here
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            loadState(savedInstanceState)
        }

        // updates your score when it loaded from previous fragment
        score.observe(viewLifecycleOwner) { score ->
            binding.tvScorePoints.text = ("$score Points")
        }

        // shows high score label if it's a new record
        isHighScore.observe(viewLifecycleOwner) { isHighScore ->
            binding.tvHighscore.visibility = if (isHighScore) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }

        // return to menu
        binding.menuButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, MenuFragment())
                .commit()
        }

        // restart a game
        binding.restartButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, SingleGameFragment())
                .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveState(outState)
    }

    // saves data to bundle
    private fun saveState(outState: Bundle) {
        val sc = score.value
        if (sc != null) {
            outState.putInt(KEY_SCORE, sc)
        }
        val ihs = isHighScore.value
        if (ihs != null) {
            outState.putBoolean(KEY_IS_HIGH_SCORE, ihs)
        }
    }

    // loads data from bundle
    private fun loadState(bundle: Bundle) {
        score.postValue(bundle.getInt(KEY_SCORE))
        isHighScore.postValue(bundle.getBoolean(KEY_IS_HIGH_SCORE))
    }

    companion object {

        const val KEY_SCORE = "score"
        const val KEY_IS_HIGH_SCORE = "is_high_score"

        // calls in SingleGameFragment.kt to create this fragment with arguments
        fun newInstance(score: Int, isHighScore: Boolean): SingleGameFinishedFragment {
            return SingleGameFinishedFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_SCORE, score)
                    putBoolean(KEY_IS_HIGH_SCORE, isHighScore)
                }
            }
        }
    }
}
