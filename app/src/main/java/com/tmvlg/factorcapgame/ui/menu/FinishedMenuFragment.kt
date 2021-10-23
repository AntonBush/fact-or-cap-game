package com.tmvlg.factorcapgame.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentFinishedMenuBinding
import com.tmvlg.factorcapgame.ui.menu.FinishedMenuFragment.Companion.newInstance
import com.tmvlg.factorcapgame.ui.singlegame.SingleGameFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FinishedMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FinishedMenuFragment : Fragment() {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFinishedMenuBinding.inflate(inflater, container, false)

        binding.singleGameButton.setOnClickListener(){
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.main_fragment_container,menufragment)
//                .commit()
        }
        binding.statButton.setOnClickListener(){

        }
        binding.leaderboardButton.setOnClickListener(){

        }
        binding.changeLanguageButton.setOnClickListener(){

        }
        binding.changeUsernameButton.setOnClickListener(){

        }
        binding.createRoomButton.setOnClickListener(){

        }
        binding.joinRoomButton.setOnClickListener(){

        }
        binding.multiplayerGameButton.setOnClickListener(){

        }
        return binding.root
        //return inflater.inflate(R.layout.fragment_finished_menu, container, false)
    }


    // Set the onClickListener for the submitButton
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FinishedMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FinishedMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}