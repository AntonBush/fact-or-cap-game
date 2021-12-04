package com.tmvlg.factorcapgame.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentLeaderboardBinding
import com.tmvlg.factorcapgame.ui.MainActivity
import com.tmvlg.factorcapgame.ui.menu.MenuFragment

class LeaderboardFragment : Fragment() {

    private lateinit var viewModel: LeaderboardViewModel
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding: FragmentLeaderboardBinding

        get() = _binding ?: throw IllegalStateException("null binding at $this")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)

        binding.pageContainer.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_change))

        binding.returnButton.setOnClickListener() {
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment_container, MenuFragment())
                .commit()
        }
        binding.leaderboardRefreshButton.setOnClickListener(){
            if ((this.activity as MainActivity).soundEnabled)(this.activity as MainActivity).snapSE.start()

            viewModel.loadDataFromDB()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[LeaderboardViewModel::class.java]
        viewModel.username = (activity as MainActivity).username
        viewModel.loadDataFromDB()

        val recyclerView = binding.gamesLeaderboardList
        val adapter = PlayerListAdapter()
        viewModel.adapter = adapter
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
