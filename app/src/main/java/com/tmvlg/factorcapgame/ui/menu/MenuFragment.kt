package com.tmvlg.factorcapgame.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentMenuBinding
import com.tmvlg.factorcapgame.ui.singlegame.SingleGameFragment
import com.tmvlg.factorcapgame.ui.statisitics.StatisticsFragment

class MenuFragment : Fragment() {

    private lateinit var viewModel: MenuViewModel

    private var _binding: FragmentMenuBinding? = null
    private val binding: FragmentMenuBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        val username = arguments?.getString("Username").toString()

        if (username == "") {
            binding.signInLayoutUnauthorized.root.visibility = View.VISIBLE
            binding.signInLayoutAuthorized.root.visibility = View.INVISIBLE
        } else {
            binding.signInLayoutUnauthorized.root.visibility = View.INVISIBLE
            binding.signInLayoutAuthorized.root.visibility = View.VISIBLE
            binding.signInLayoutAuthorized.usernameTextview.text = "Hello, $username"
        }

        val singleGameFragmentWU = SingleGameFragment()
        singleGameFragmentWU.arguments = Bundle().apply {
            putString("Username", username)
        }
        val statisticsFragmentWU = StatisticsFragment()
        statisticsFragmentWU.arguments = Bundle().apply {
            putString("Username", username)
        }
        binding.signInLayoutUnauthorized.googleSignInCardview.setOnClickListener {
            //TODO("Реализация входа в аккаунт")
            Toast.makeText(this.context, "In development", Toast.LENGTH_SHORT).show()
        }
        binding.signInLayoutAuthorized.signedUserCardview.setOnClickListener {
            binding.signInLayoutAuthorized.signOutLayout.visibility =
                binding.signInLayoutAuthorized.signOutLayout.visibility.xor(8)
        }
        binding.signInLayoutAuthorized.signOutLayout.setOnClickListener {
            //TODO("Реализация выхода из аккаунта")
            Toast.makeText(this.context, "In development", Toast.LENGTH_SHORT).show()
        }
        binding.singleGameButton.setOnClickListener() {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, singleGameFragmentWU)
                .commit()
        }
        binding.statButton.setOnClickListener() {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, statisticsFragmentWU)
                .commit()
        }
        binding.leaderboardButton.setOnClickListener() {
            Toast.makeText(this.context, "In development", Toast.LENGTH_SHORT).show()
        }
        binding.changeLanguageButton.setOnClickListener() {
            Toast.makeText(this.context, "In development", Toast.LENGTH_SHORT).show()
        }
        binding.createRoomButton.setOnClickListener() {
            Toast.makeText(this.context, "In development", Toast.LENGTH_SHORT).show()
        }
        binding.joinRoomButton.setOnClickListener() {
            Toast.makeText(this.context, "In development", Toast.LENGTH_SHORT).show()
        }
        binding.multiplayerGameButton.setOnClickListener() {
            toggleMultiplayerButton()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MenuViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toggleMultiplayerButton() {
        val visibility = binding.joinRoomButton.visibility.xor(4)
        val upperAnim = if (visibility == View.VISIBLE) {
            AnimationUtils.loadAnimation(requireContext(), R.anim.multiplayer_in_upper)
        } else {
            AnimationUtils.loadAnimation(requireContext(), R.anim.multiplayer_out_upper)
        }
        val lowerAnim = if (visibility == View.VISIBLE) {
            AnimationUtils.loadAnimation(requireContext(), R.anim.multiplayer_in_lower)
        } else {
            AnimationUtils.loadAnimation(requireContext(), R.anim.multiplayer_out_lower)
        }
        binding.createRoomButton.visibility = visibility
        binding.createRoomButton.startAnimation(upperAnim)
        binding.joinRoomButton.visibility = visibility
        binding.joinRoomButton.startAnimation(lowerAnim)
    }
}
