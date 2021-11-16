package com.tmvlg.factorcapgame.data.repository.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.RuntimeException

class GameViewModelFactory(private val gameRepository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java))
            return GameViewModel(gameRepository) as T
        throw RuntimeException("Unknown view model class $modelClass")
    }
}