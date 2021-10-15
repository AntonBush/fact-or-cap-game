package com.tmvlg.factorcapgame.ui.singlegame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.databinding.FragmentSingleGameBinding
import java.lang.RuntimeException

class SingleGameFragment : Fragment() {

    private lateinit var viewModel: SingleGameViewModel

    private var _binding: FragmentSingleGameBinding? = null
    private val binding: FragmentSingleGameBinding
        get() = _binding ?: throw RuntimeException("null binding at $this")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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

        viewModel = ViewModelProvider(this)[SingleGameViewModel::class.java]

    }

}