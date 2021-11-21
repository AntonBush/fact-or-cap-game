package com.tmvlg.factorcapgame.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import com.tmvlg.factorcapgame.ui.multiplayergame.MultiplayerGameFinishedViewModel

class MenuViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java))
            return MenuViewModel(userRepository) as T
        throw IllegalArgumentException("Unknown view model class $modelClass")
    }
}