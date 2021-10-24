package com.tmvlg.factorcapgame.ui.statisitics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentStatisticsBinding
import com.tmvlg.factorcapgame.ui.menu.MenuFragment

class StatisticsFragment : Fragment() {

    private lateinit var viewModel: StatisticsViewModel

    private var _binding: FragmentStatisticsBinding? = null
    private val binding: FragmentStatisticsBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        binding.returnButton.setOnClickListener(){
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container,MenuFragment())
                .commit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
