package com.tmvlg.factorcapgame.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat.setAlpha
import androidx.fragment.app.Fragment
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.ui.menu.CreateUsernameFragment
import com.tmvlg.factorcapgame.ui.menu.MenuFragment
import com.tmvlg.factorcapgame.ui.singlegame.SingleGameFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment : Fragment
        if (hasValidUsername())
            fragment = MenuFragment()
        else
            fragment = CreateUsernameFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container,fragment)
            .commit()
    }

    private fun hasValidUsername() : Boolean {
        // Здесь смотреть не зарегистрировался ли уже пользователь
        return false
    }

}
