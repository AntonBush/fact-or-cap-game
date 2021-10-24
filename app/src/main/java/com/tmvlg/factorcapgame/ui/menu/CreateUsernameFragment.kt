package com.tmvlg.factorcapgame.ui.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.databinding.FragmentCreateUsernameBinding
import android.view.KeyEvent

import android.widget.EditText
import android.widget.TextView
import com.tmvlg.factorcapgame.ui.singlegame.SingleGameFragment


class CreateUsernameFragment : Fragment() {

    private lateinit var viewModel: MenuViewModel

    private var _binding: FragmentCreateUsernameBinding? = null
    private val binding: FragmentCreateUsernameBinding
        get() = _binding ?: throw IllegalStateException("null binding at $this")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreateUsernameBinding.inflate(inflater, container, false)

        binding.confirmUsernameButton.setOnClickListener() {
            val usernameString = binding.createUsernameEdittext.text.toString()
            if (checkValidUsername(usernameString)) {
                val menuFragmentWU = MenuFragment()
                menuFragmentWU.arguments = Bundle().apply {
                    putString("Username", usernameString)
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, menuFragmentWU)
                    .commit()
            }
            else {
                //Добавить правильное сообщение пользователю
                Toast.makeText(this.context,"Bad username", Toast.LENGTH_SHORT).show()
            }
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

    private fun checkValidUsername( name: String ) : Boolean {
        // Сюда вставить проверку на корректность имени пользователя
        return true
    }

}
