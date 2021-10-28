package com.tmvlg.factorcapgame.ui.statisitics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.FactOrCapApplication
import com.tmvlg.factorcapgame.databinding.FragmentStatisticsBinding
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.ui.menu.MenuFragment

class StatisticsFragment : Fragment() {

    private lateinit var viewModel: StatisticsViewModel

    private var _binding: FragmentStatisticsBinding? = null
    private val binding: FragmentStatisticsBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")


    val statisticsViewModelFactory by lazy {
        StatisticsViewModelFactory(
            (activity?.application as FactOrCapApplication).userRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        val menuFragmentWU = MenuFragment()
        menuFragmentWU.arguments = Bundle().apply {
            putString("Username", arguments?.getString("Username").toString())
        }
        binding.returnButton.setOnClickListener() {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, menuFragmentWU)
                .commit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, statisticsViewModelFactory)[StatisticsViewModel::class.java]

        observeStatisticViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeStatisticViewModel(){
        viewModel.totalGames.observe(viewLifecycleOwner){
            binding.totalGamesValue.text = it.toString()
        }
        viewModel.highestScore.observe(viewLifecycleOwner){
            binding.highestScoreValue.text = it.toString()
        }
        viewModel.lastScore.observe(viewLifecycleOwner){
            binding.lastScoreValue.text = it.toString()
        }
        viewModel.averageScore.observe(viewLifecycleOwner){
            binding.averageScoreValue.text = it.toString()
        }
    }
}
