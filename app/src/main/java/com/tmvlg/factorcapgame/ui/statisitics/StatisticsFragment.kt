package com.tmvlg.factorcapgame.ui.statisitics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentStatisticsBinding
import com.tmvlg.factorcapgame.ui.MainActivity
import com.tmvlg.factorcapgame.ui.menu.MenuFragment

class StatisticsFragment : Fragment() {
    private val viewModel: StatisticsViewModel by viewModels {
        val app = activity?.application as FactOrCapApplication
        return@viewModels StatisticsViewModelFactory(
                app.userRepository,
                app.gameRepository
        )
    }

    private var _binding: FragmentStatisticsBinding? = null
    private val binding: FragmentStatisticsBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    val adapter = GameListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pageContainer.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_change))

        val recyclerView = binding.gamesStatisticsList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        binding.returnButton.isSoundEffectsEnabled = false
        binding.statisticsToggleButton.isSoundEffectsEnabled = false
        binding.gamesToggleButton.isSoundEffectsEnabled = false

        setupListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        binding.returnButton.setOnClickListener {
            (activity as MainActivity).snapSEStart()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, MenuFragment())
                .commit()
        }
        binding.statisticsToggleButton.setOnClickListener {
            (activity as MainActivity).snapSEStart()
            if (binding.grid.visibility == View.VISIBLE) {
                binding.grid.visibility = View.GONE
                binding.gamesStatisticsList.visibility = View.VISIBLE
                binding.statisticsToggleButton.rotation = FLOAT_ZERO
                binding.gamesToggleButton.rotation = FLOAT_90
            } else {
                binding.grid.visibility = View.VISIBLE
                binding.gamesStatisticsList.visibility = View.GONE
                binding.statisticsToggleButton.rotation = FLOAT_90
                binding.gamesToggleButton.rotation = FLOAT_ZERO
            }
        }
        binding.gamesToggleButton.setOnClickListener {
            (activity as MainActivity).snapSEStart()
            if (binding.gamesStatisticsList.visibility == View.VISIBLE) {
                binding.grid.visibility = View.VISIBLE
                binding.gamesStatisticsList.visibility = View.GONE
                binding.gamesToggleButton.rotation = FLOAT_ZERO
                binding.statisticsToggleButton.rotation = FLOAT_90
            } else {
                binding.gamesStatisticsList.visibility = View.VISIBLE
                binding.grid.visibility = View.GONE
                binding.gamesToggleButton.rotation = FLOAT_90
                binding.statisticsToggleButton.rotation = FLOAT_ZERO
            }
        }
    }

    private fun observeViewModel() {
        viewModel.allGames.observe(viewLifecycleOwner) { games ->
            games.let {
                adapter.updateList(it)
            }
        }
        viewModel.totalGames.observe(viewLifecycleOwner) {
            binding.totalGamesValue.text = it.toString()
        }
        viewModel.highestScore.observe(viewLifecycleOwner) {
            binding.highestScoreValue.text = it.toString()
        }
        viewModel.lastScore.observe(viewLifecycleOwner) {
            binding.lastScoreValue.text = it.toString()
        }
        viewModel.averageScore.observe(viewLifecycleOwner) {
            binding.averageScoreValue.text = it.toString()
        }
    }

    companion object {
        const val FLOAT_ZERO = 0f
        const val FLOAT_90 = 90f
    }
}
